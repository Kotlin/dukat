import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.translatorString.StringTranslator


private fun ModuleModel.flattenDeclarations(): List<ModuleModel> {
    return (listOf(this) + sumbodules.flatMap { submodule -> submodule.flattenDeclarations() })
            .filter { module -> module.declarations.isNotEmpty() }
}

fun translateModule(docRoot: ModuleModel): List<String> {
    return docRoot.flattenDeclarations().map { module ->
        val stringTranslator = StringTranslator()
        stringTranslator.process(module)
        stringTranslator.output()
    }
}


