package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.factory.convert
import org.jetbrains.dukat.tsmodelproto.SourceBundleDeclarationProto

class JsRuntimeByteArrayTranslator(
        private val lowerer: ECMAScriptLowerer
) : InputTranslator<ByteArray> {
    constructor(nameResolver: ModuleNameResolver) : this(TypescriptLowerer(nameResolver))

    private val logger = Logging.logger(JsRuntimeByteArrayTranslator::class.simpleName.toString())

    fun lower(sourceBundle: SourceBundleDeclaration): SourceBundleModel {
        return lowerer.lower(sourceBundle)
    }

    fun parse(data: ByteArray): SourceBundleDeclaration {
        return SourceBundleDeclarationProto.parseFrom(data).convert()
    }

    override fun translate(data: ByteArray): SourceBundleModel {
        return lower(parse(data))
    }
}
