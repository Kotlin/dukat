import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.translatorString.StringTranslator


private fun ModuleModel.flattenDeclarations(): List<ModuleModel> {
    return (listOf(this) + sumbodules.flatMap { submodule -> submodule.flattenDeclarations() })
            .filter { module -> module.declarations.isNotEmpty() }
}

fun translateModule(docRoot: ModuleModel): String {
    val translated = docRoot.flattenDeclarations().map {
        val stringTranslator = StringTranslator()
        stringTranslator.process(it)
        stringTranslator.output()
    }

    return if (translated.isEmpty()) {
        "// NO DECLARATIONS"
    } else {
        translated.joinToString("""

// ------------------------------------------------------------------------------------------
""")
    }

}

