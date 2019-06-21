import org.jetbrains.dukat.ast.model.nodes.processing.process
import org.jetbrains.dukat.ast.model.nodes.processing.shiftLeft
import org.jetbrains.dukat.ast.model.nodes.processing.translate
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

private fun unescape(name: String): String {
    return name.replace("(?:^`)|(?:`$)".toRegex(), "")
}

private fun NameEntity.fileNameFragment(): String? {
    val unprefixedName = shiftLeft()

    return if (unprefixedName == null) {
        null
    } else {
        unprefixedName.process(::unescape).translate()
    }
}

private fun SourceFileModel.resolveAsTargetName(packageName: NameEntity): String  {
    val sourceFile = File(fileName)
    val sourceFileName = sourceFile.name
    val ktFileNamePrefix =
            if (sourceFileName.endsWith(TS_DECLARATION_EXTENSION)) {
                sourceFileName.removeSuffix(TS_DECLARATION_EXTENSION)
            } else sourceFile.name

    var res = ktFileNamePrefix

    packageName.fileNameFragment()?.let { packageFragment ->
        res += ".${packageFragment}"
    }

    name?.let {
        res += ".${it}"
    }

    return res
}

private fun translateModule(sourceFile: SourceFileModel): List<ModuleTranslationUnit> {
    val docRoot = sourceFile.root
    return docRoot.flattenDeclarations().map { module ->
        val stringTranslator = StringTranslator()
        stringTranslator.process(module)
        ModuleTranslationUnit(sourceFile.resolveAsTargetName(module.name), sourceFile.fileName, module.name, stringTranslator.output())
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
        val sourceKey = Pair(sourceFile.fileName, sourceFile.root.name)
        if (!visited.contains(sourceKey)) {
            visited.add(sourceKey)
            translateModule(sourceFile)
        } else {
            null
        }
    }.flatten()
}