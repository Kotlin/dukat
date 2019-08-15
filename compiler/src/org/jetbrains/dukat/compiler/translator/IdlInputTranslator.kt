package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.commonLowerings.lowerOverrides
import org.jetbrains.dukat.idlLowerings.addConstructors
import org.jetbrains.dukat.idlLowerings.addImportsForReferencedFiles
import org.jetbrains.dukat.idlLowerings.resolveImplementsStatements
import org.jetbrains.dukat.idlLowerings.resolveMixins
import org.jetbrains.dukat.idlLowerings.resolvePartials
import org.jetbrains.dukat.idlLowerings.resolveTypedefs
import org.jetbrains.dukat.idlLowerings.resolveTypes
import org.jetbrains.dukat.idlLowerings.specifyDefaultValues
import org.jetbrains.dukat.idlModels.process
import org.jetbrains.dukat.idlParser.parseIDL
import org.jetbrains.dukat.idlReferenceResolver.IdlReferencesResolver
import org.jetbrains.dukat.translator.InputTranslator

class IdlInputTranslator(private val nameResolver: IdlReferencesResolver): InputTranslator {

    override fun translate(fileName: String): SourceSetModel {
        return parseIDL(fileName, nameResolver)
                .resolvePartials()
                .addConstructors()
                .resolveTypedefs()
                .specifyDefaultValues()
                .resolveImplementsStatements()
                .resolveMixins()
                .resolveTypes()
                .process()
                .lowerOverrides()
                .addImportsForReferencedFiles()
    }

}