import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translator.TranslationErrorFileNotFound
import org.jetbrains.dukat.translator.TranslationErrorInvalidFile
import org.jetbrains.dukat.translator.TranslationUnitResult
import org.jetbrains.dukat.translatorString.StringTranslator
import org.jetbrains.dukat.translatorString.TS_DECLARATION_EXTENSION
import java.io.File

private typealias SourceUnit = Pair<String, NameEntity>

private fun translateModule(sourceFile: SourceFileModel): List<ModuleTranslationUnit> {
    val docRoot = sourceFile.root
    return docRoot.flattenDeclarations().map { module ->
        val stringTranslator = StringTranslator()
        stringTranslator.process(module)
        ModuleTranslationUnit(sourceFile.fileName, module.packageName, stringTranslator.output())
    }
}

fun translateModule(fileName: String, translator: InputTranslator): List<TranslationUnitResult> {
    if (!fileName.endsWith(TS_DECLARATION_EXTENSION)) {
        return listOf(TranslationErrorInvalidFile(fileName))
    }

    if (!File(fileName).exists()) {
        return listOf(TranslationErrorFileNotFound(fileName))
    }

    val sourceSet =
            translator.translate(fileName)

    val visited = mutableSetOf<SourceUnit>()

    return sourceSet.sources.mapNotNull { sourceFile ->
        // TODO: investigate whether it's safe to check just fileName
        val sourceKey = Pair(sourceFile.fileName, sourceFile.root.packageName)
        if (!visited.contains(sourceKey)) {
            visited.add(sourceKey)
            translateModule(sourceFile)
        } else {
            null
        }
    }.flatten()
}