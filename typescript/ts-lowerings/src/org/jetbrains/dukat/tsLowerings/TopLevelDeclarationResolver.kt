package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration


private fun ModuleDeclaration.scan(topLevelDeclarationsMap: MutableMap<String, TopLevelDeclaration>) {
    this.declarations.forEach {
        when (it) {
            is ClassLikeDeclaration -> {
                topLevelDeclarationsMap[it.uid] = it
            }
            is ModuleDeclaration -> it.scan(topLevelDeclarationsMap)
        }
    }
}

class TopLevelDeclarationResolver(private val sourceSetDeclaration: SourceSetDeclaration) {
    @OptIn(ExperimentalStdlibApi::class)
    private val declarationMap = buildMap<String, TopLevelDeclaration> {
        sourceSetDeclaration.sources.forEach { it.root.scan(this) }
    }

    fun resolve(reference: ReferenceDeclaration?): TopLevelDeclaration? {
        return reference?.uid?.let { declarationMap.get(it) }
    }
}