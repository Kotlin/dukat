import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.translatorString.StringTranslator

fun translateModule(docRoot: ModuleModel): List<String> {
    return docRoot.flattenDeclarations().map { module ->
        val stringTranslator = StringTranslator()
        stringTranslator.process(module)
        stringTranslator.output()
    }
}


