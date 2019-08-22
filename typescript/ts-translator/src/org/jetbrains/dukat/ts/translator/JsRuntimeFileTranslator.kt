package org.jetbrains.dukat.ts.translator

import dukat.ast.proto.Declarations
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.factory.convert
import java.io.InputStream
import java.util.concurrent.TimeUnit

class JsRuntimeFileTranslator(
        override val packageName: NameEntity,
        override val moduleNameResolver: ModuleNameResolver,
        private val translatorPath: String,
        private val libPath: String,
        private val nodePath: String
) : TypescriptInputTranslator<String> {

    private val logger = Logging.logger(JsRuntimeByteArrayTranslator::class.simpleName.toString())

    private fun translateAsInputStream(fileName: String, packageName: NameEntity): InputStream {
        logger.debug("${packageName} ${translatorPath} ${fileName}")
        val proc = ProcessBuilder(nodePath, translatorPath, libPath, "--std-out", fileName).start();
        proc.waitFor(60, TimeUnit.SECONDS)

        if (proc.exitValue() > 0) {
            logger.debug(proc.errorStream.bufferedReader().readText())
        }

        return proc.inputStream.buffered()
    }

    private fun translateFile(fileName: String, packageName: NameEntity): SourceBundleDeclaration {
        val proto = Declarations.SourceSetBundleProto.parseFrom(translateAsInputStream(fileName, packageName))
        logger.debug("${proto}")
        return proto.convert()
    }

    override fun translate(data: String): SourceBundleModel {
        return lower(translateFile(data, packageName))
    }
}
