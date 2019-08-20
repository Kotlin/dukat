package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.commonLowerings.lowerOverrides
import org.jetbrains.dukat.commonLowerings.omitStdLib
import org.jetbrains.dukat.commonLowerings.merge.escapeIdentificators
import org.jetbrains.dukat.idlLowerings.addConstructors
import org.jetbrains.dukat.idlLowerings.addImportsForReferencedFiles
import org.jetbrains.dukat.idlLowerings.addItemArrayLike
import org.jetbrains.dukat.idlLowerings.markAbstractOrOpen
import org.jetbrains.dukat.idlLowerings.resolveImplementsStatements
import org.jetbrains.dukat.idlLowerings.resolvePartials
import org.jetbrains.dukat.idlLowerings.resolveTypedefs
import org.jetbrains.dukat.idlLowerings.resolveTypes
import org.jetbrains.dukat.idlLowerings.specifyDefaultValues
import org.jetbrains.dukat.idlModels.process
import org.jetbrains.dukat.idlParser.parseIDL
import org.jetbrains.dukat.idlReferenceResolver.IdlReferencesResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import org.jetbrains.dukat.ts.translator.createJsByteArrayTranslator
import java.io.File

class IdlInputTranslator(private val nameResolver: IdlReferencesResolver): InputTranslator<String> {

    fun translateSet(fileName: String): SourceSetModel {
        return parseIDL(fileName, nameResolver)
                .resolvePartials()
                .addConstructors()
                .resolveTypedefs()
                .specifyDefaultValues()
                .resolveImplementsStatements()
                .addItemArrayLike()
                .resolveTypes()
                .markAbstractOrOpen()
                .process()
                .lowerOverrides()
                .escapeIdentificators()
                .addImportsForReferencedFiles()
                .omitStdLib()
    }

    override fun translate(fileName: String): SourceBundleModel {
        return SourceBundleModel(listOf(translateSet(fileName)))
    }

}