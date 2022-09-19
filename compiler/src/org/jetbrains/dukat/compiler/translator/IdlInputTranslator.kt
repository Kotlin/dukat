package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.commonLowerings.AddExplicitGettersAndSetters
import org.jetbrains.dukat.idlLowerings.addConstructors
import org.jetbrains.dukat.idlLowerings.addImportsForUsedPackages
import org.jetbrains.dukat.idlLowerings.addItemArrayLike
import org.jetbrains.dukat.idlLowerings.addKDocs
import org.jetbrains.dukat.idlLowerings.addMissingMembers
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
import org.jetbrains.dukat.model.commonLowerings.*
import org.jetbrains.dukat.translator.InputTranslator

private fun alwaysPublic(): VisibilityModifierResolver = object : VisibilityModifierResolver {
    override fun resolve(): VisibilityModifierModel = VisibilityModifierModel.PUBLIC
}

class IdlInputTranslator(private val nameResolver: IdlReferencesResolver, private val dynamicAsType: Boolean = false) : InputTranslator<String> {
    private val translationContext: TranslationContext = TranslationContext()

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
                .lower(
                        ModelContextAwareLowering(translationContext),
                        LowerOverrides(translationContext),
                        EscapeIdentificators(),
                        AddExplicitGettersAndSetters(),
                )
                .let { if (dynamicAsType) it.lower(ReplaceDynamics()) else it }
                .addKDocs()
                .relocateDeclarations()
                .resolveTopLevelVisibility(alwaysPublic())
                .addImportsForUsedPackages()
                .omitStdLib()
    }

    override fun translate(data: String): SourceSetModel {
        return translateSet(data)
    }

}