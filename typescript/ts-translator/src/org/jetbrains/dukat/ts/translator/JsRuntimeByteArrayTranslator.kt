package org.jetbrains.dukat.ts.translator

import dukat.ast.proto.Declarations
import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.factory.convert

class JsRuntimeByteArrayTranslator(
        private val lowerer: ECMAScriptLowerer
) : InputTranslator<ByteArray> {
    constructor(nameResolver: ModuleNameResolver) : this(TypescriptLowerer(nameResolver))

    private val logger = Logging.logger(JsRuntimeByteArrayTranslator::class.simpleName.toString())

    fun lower(sourceSet: SourceSetDeclaration): SourceSetModel {
        return lowerer.lower(sourceSet)
    }

    private fun lower(sourceBundle: SourceBundleDeclaration): SourceBundleModel {
        return lowerer.lower(sourceBundle)
    }

    fun parse(data: ByteArray): SourceBundleDeclaration {
        return Declarations.SourceSetBundleProto.parseFrom(data).convert()
    }

    override fun translate(data: ByteArray): SourceBundleModel {
        return lower(parse(data))
    }
}
