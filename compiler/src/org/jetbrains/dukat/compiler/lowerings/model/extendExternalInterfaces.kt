package org.jetbrains.dukat.compiler.lowerings.model

import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.model.SourceFileModel
import org.jetbrains.dukat.ast.model.model.SourceSetModel
import org.jetbrains.dukat.ast.model.model.getSourceFile

private sealed class Origin
private data class External(val fileName: String)  : Origin()
private class Internal : Origin()


private fun SourceFileModel.collectDeclarationsOrigin(owner: SourceSetModel, declarationsMap: MutableMap<String, Origin>, external: Boolean) {

    referencedFiles.forEach { refFileName ->
        owner.getSourceFile(refFileName.value)?.let { sourceFile ->
            sourceFile.collectDeclarationsOrigin(owner, declarationsMap, true)
        }
    }

    root.declarations.forEach { declaration ->
        if (declaration is InterfaceModel) {
            val declarationName = declaration.name
            if (!declarationsMap.containsKey(declarationName)) {
                if (external) {
                    declarationsMap.put(declarationName, External(fileName))
                } else {
                    declarationsMap.put(declarationName, Internal())
                }
            }
        }
    }
}

fun SourceSetModel.extendExternalInterfaces(): SourceSetModel {

    sources.forEach { source ->
        val interfaceDeclarationOrigin = mutableMapOf<String, Origin>()
        source.collectDeclarationsOrigin(this, interfaceDeclarationOrigin, false)
    }

    return this
}