package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.factory.convert
import org.jetbrains.dukat.tsmodelproto.SourceSetDeclarationProto
import java.io.InputStream
import java.util.concurrent.TimeUnit

class JsRuntimeFileTranslator(
        private val lowerer: ECMAScriptLowerer,
        private val translatorPath: String,
        private val libPath: String,
        private val nodePath: String
) : InputTranslator<String> {
    private val logger = Logging.logger(JsRuntimeByteArrayTranslator::class.simpleName.toString())

    private fun translateAsInputStream(fileName: String): InputStream {
        val proc = ProcessBuilder(nodePath, translatorPath, libPath, "--std-out", fileName).start();
        proc.waitFor(60, TimeUnit.SECONDS)

        if (proc.exitValue() > 0) {
            logger.debug(proc.errorStream.bufferedReader().readText())
        }

        return proc.inputStream.buffered()
    }

    private fun translateFile(fileName: String): SourceSetDeclaration {
        return SourceSetDeclarationProto.parseFrom(translateAsInputStream(fileName)).convert()
    }

    override fun translate(data: String): SourceSetModel {
        return lowerer.lower(translateFile(data))
    }
}
