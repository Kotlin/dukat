package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.factory.convert
import org.jetbrains.dukat.tsmodelproto.SourceSetDeclarationProto

class JsRuntimeByteArrayTranslator(
        private val lowerer: ECMAScriptLowerer
) : InputTranslator<ByteArray> {
    fun lower(sourceBundle: SourceSetDeclaration): SourceSetModel {
        return lowerer.lower(sourceBundle)
    }

    fun parse(data: ByteArray): SourceSetDeclaration {
        return SourceSetDeclarationProto.parseFrom(data).convert()
    }

    override fun translate(data: ByteArray): SourceSetModel {
        return lower(parse(data))
    }
}
