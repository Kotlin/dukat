package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.interop.InteropEngine
import org.jetbrains.dukat.interop.graal.InteropGraal
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.tsinterop.ExportContent
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.factory.AstFactory
import java.util.function.Supplier

private fun InteropEngine.loadAstBuilder() {
    val fileResolver = FileResolver()
    eval(fileResolver.readResource("js/converter.js"))
}

private fun createGraalInterop(): InteropGraal {
    val engine = InteropGraal()

    engine.put("createAstFactory", Supplier { AstFactory() })
    engine.put("createExportContent", Supplier { ExportContent<Any>() })
    engine.put("createFileResolver", Supplier { FileResolver() })
    engine.put("createLogger", java.util.function.Function<String, Logging> { name -> Logging.logger(name) })

    engine.loadAstBuilder()

    return engine
}

class TranslatorGraal(
        private val engine: InteropGraal,
        override val packageName: NameEntity,
        override val moduleNameResolver: ModuleNameResolver
) : TypescriptInputTranslator {
    override fun translateFile(fileName: String, packageName: NameEntity): SourceSetDeclaration {
        return engine.callFunction("main", fileName, packageName)
    }

    override fun release() {}
}

fun createGraalTranslator(packageName: NameEntity, moduleNameResolver: ModuleNameResolver) = TranslatorGraal(createGraalInterop(), packageName, moduleNameResolver)