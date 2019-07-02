package org.jetbrains.dukat.compiler.translator

import org.antlr.v4.runtime.CharStreams
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.idl.parse
import org.jetbrains.dukat.translator.InputTranslator

class IdlInputTranslator: InputTranslator {

    override fun translate(fileName: String): SourceSetModel {
        return parse(CharStreams.fromFileName(fileName, Charsets.UTF_8))
    }

}