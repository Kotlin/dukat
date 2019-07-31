package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.idlModels.process
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.commonLowerings.lowerOverrides
import org.jetbrains.dukat.idlLowerings.*
import org.jetbrains.dukat.idlParser.parseIDL
import org.jetbrains.dukat.translator.InputTranslator

class IdlInputTranslator: InputTranslator {

    override fun translate(fileName: String): SourceSetModel {
        return parseIDL(fileName)
                .addConstructors()
                .resolveTypedefs()
                .specifyDefaultValues()
                .resolveImplementsStatements()
                .resolveTypes()
                .process()
                .lowerOverrides()
    }

}