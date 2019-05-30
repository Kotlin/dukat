import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translatorString.ModuleTranslationUnit
import org.jetbrains.dukat.translatorString.StringTranslator

private fun translateModule(docRoot: ModuleModel): List<ModuleTranslationUnit> {
    return docRoot.flattenDeclarations().map { module ->
        val stringTranslator = StringTranslator()
        stringTranslator.process(module)
        ModuleTranslationUnit(module.packageName, stringTranslator.output())
    }
}

fun translateModule(fileName: String, translator: InputTranslator, rootPackageName: NameEntity): List<ModuleTranslationUnit> {
    val sourceSet =
            translator.translate(fileName, rootPackageName)

    val visited = mutableSetOf<NameEntity>()

    return sourceSet.sources.mapNotNull { sourceFile ->
        if (!visited.contains(sourceFile.root.packageName)) {
            visited.add(sourceFile.root.packageName)
            translateModule(sourceFile.root)
        } else {
            null
        }
    }.flatten()
}