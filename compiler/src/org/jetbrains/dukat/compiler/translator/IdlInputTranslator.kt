package org.jetbrains.dukat.compiler.translator

import main.org.jetbrains.dukat.idlModels.process
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.idlDeclarations.parseIDL
import org.jetbrains.dukat.translator.InputTranslator

class IdlInputTranslator: InputTranslator {

    override fun translate(fileName: String): SourceSetModel {
        return parseIDL(fileName).process()
    }

}