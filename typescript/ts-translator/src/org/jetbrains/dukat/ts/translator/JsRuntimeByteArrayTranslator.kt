package org.jetbrains.dukat.ts.translator

import dukat.ast.proto.Declarations
import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.factory.convert

class JsRuntimeByteArrayTranslator(
        override val moduleNameResolver: ModuleNameResolver
) : TypescriptInputTranslator<ByteArray> {

    private val logger = Logging.logger(JsRuntimeByteArrayTranslator::class.simpleName.toString())

    fun parse(data: ByteArray): SourceBundleDeclaration {
        return Declarations.SourceSetBundleProto.parseFrom(data).convert()
    }

    override fun translate(data: ByteArray): SourceBundleModel {
        return lower(parse(data))
    }
}
