package org.jetbrains.dukat.compiler.translator

import org.antlr.v4.runtime.CharStreams
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.idlDeclarations.parseIDL
import org.jetbrains.dukat.translator.InputTranslator

class IdlInputTranslator: InputTranslator {

    override fun translate(fileName: String): SourceSetModel {
        //TODO
        return SourceSetModel(listOf())
    }

}