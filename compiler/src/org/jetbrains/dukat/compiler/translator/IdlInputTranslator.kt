package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.idlLowerings.addKDocs
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.commonLowerings.addExplicitGettersAndSetters
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.model.commonLowerings.lowerOverrides
import org.jetbrains.dukat.idlLowerings.addConstructors
import org.jetbrains.dukat.idlLowerings.addImportsForReferencedFiles
import org.jetbrains.dukat.idlLowerings.addMissingMembers
import org.jetbrains.dukat.idlLowerings.addItemArrayLike
import org.jetbrains.dukat.idlLowerings.addOverloadsForCallbacks
import org.jetbrains.dukat.idlLowerings.markAbstractOrOpen
import org.jetbrains.dukat.idlLowerings.omitStdLib
import org.jetbrains.dukat.idlLowerings.relocateDeclarations
import org.jetbrains.dukat.idlLowerings.resolveImplementsStatements
import org.jetbrains.dukat.idlLowerings.resolveMixins
import org.jetbrains.dukat.idlLowerings.resolvePartials
import org.jetbrains.dukat.idlLowerings.resolveTypedefs
import org.jetbrains.dukat.idlLowerings.resolveTypes
import org.jetbrains.dukat.idlLowerings.specifyDefaultValues
import org.jetbrains.dukat.idlLowerings.specifyEventHandlerTypes
import org.jetbrains.dukat.idlModels.convertToModel
import org.jetbrains.dukat.idlParser.parseIDL
import org.jetbrains.dukat.idlReferenceResolver.IdlReferencesResolver
import org.jetbrains.dukat.model.commonLowerings.VisibilityModifierResolver
import org.jetbrains.dukat.model.commonLowerings.escapeIdentificators
import org.jetbrains.dukat.model.commonLowerings.resolveTopLevelVisibility
import org.jetbrains.dukat.translator.InputTranslator

private fun alwaysPublic(): VisibilityModifierResolver = object : VisibilityModifierResolver {
    override fun resolve(): VisibilityModifierModel = VisibilityModifierModel.PUBLIC
}

class IdlInputTranslator(private val nameResolver: IdlReferencesResolver): InputTranslator<String> {

    fun translateSet(fileName: String): SourceSetModel {
        return parseIDL(fileName, nameResolver)
                .resolvePartials()
                .addConstructors()
                .resolveTypedefs()
                .specifyEventHandlerTypes()
                .specifyDefaultValues()
                .resolveImplementsStatements()
                .resolveMixins()
                .addItemArrayLike()
                .resolveTypes()
                .markAbstractOrOpen()
                .addMissingMembers()
                .addOverloadsForCallbacks()
                .convertToModel()
                .lowerOverrides(null)
                .escapeIdentificators()
                .addExplicitGettersAndSetters()
                .addKDocs()
                .relocateDeclarations()
                .resolveTopLevelVisibility(alwaysPublic())
                .addImportsForReferencedFiles()
                .omitStdLib()
    }

    override fun translate(data: String): SourceBundleModel {
        return SourceBundleModel(listOf(translateSet(data)))
    }

}