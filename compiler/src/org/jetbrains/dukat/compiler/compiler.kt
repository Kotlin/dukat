package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.compiler.translator.InputTranslator
import translateModule
import java.io.File

fun compile(documentRoot: ModuleModel): String {
    var res = translateModule(documentRoot).joinToString("""

// ------------------------------------------------------------------------------------------
""") { it.joinToString("\n") }

    if (res == "") {
        res = "// NO DECLARATIONS"
    }
    return res
}

fun output(fileName: String, translator: InputTranslator): String {
    val sourceSetDeclaration = translator.translateFile(fileName)

    val sourceSet =
            translator.lower(sourceSetDeclaration)

    val sourcesMap = mutableMapOf<String, SourceFileModel>()
    sourceSet.sources.map { sourceFileDeclaration ->
        sourcesMap[sourceFileDeclaration.fileName] = sourceFileDeclaration
    }


    val fileNameNormalized = File(fileName).normalize().absolutePath

    val documentRoot = sourcesMap.get(fileNameNormalized)?.root!!
    return compile(documentRoot)
}