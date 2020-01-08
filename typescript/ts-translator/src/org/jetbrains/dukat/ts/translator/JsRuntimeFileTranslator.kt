package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.factory.convert
import org.jetbrains.dukat.tsmodelproto.SourceBundleDeclarationProto
import java.io.InputStream
import java.util.concurrent.TimeUnit

class JsRuntimeFileTranslator(
        private val lowerer: ECMAScriptLowerer,
        private val translatorPath: String,
        private val libPath: String,
        private val nodePath: String
) : InputTranslator<String> {
    constructor(
            nameResolver: ModuleNameResolver,
            translatorPath: String,
            libPath: String,
            nodePath: String
    ) : this(TypescriptLowerer(nameResolver), translatorPath, libPath, nodePath)

    private val logger = Logging.logger(JsRuntimeByteArrayTranslator::class.simpleName.toString())

    private fun translateAsInputStream(fileName: String): InputStream {
        val proc = ProcessBuilder(nodePath, translatorPath, libPath, "--std-out", fileName).start();
        proc.waitFor(60, TimeUnit.SECONDS)

        if (proc.exitValue() > 0) {
            logger.debug(proc.errorStream.bufferedReader().readText())
        }

        return proc.inputStream.buffered()
    }

    private fun translateFile(fileName: String): SourceBundleDeclaration {
        val proto = SourceBundleDeclarationProto.parseFrom(translateAsInputStream(fileName))
        logger.debug("${proto}")
        return proto.convert()
    }

    override fun translate(data: String): SourceBundleModel {
        return lowerer.lower(translateFile(data))
    }
}
