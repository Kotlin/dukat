package org.jetbrains.dukat.tsLowerings

import TopLevelDeclarationLowering
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.ConstructSignatureDeclaration
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class ReplaceExpressionExtensionLowering(private val topLevelDeclarationResolver: TopLevelDeclarationResolver) : TopLevelDeclarationLowering {
    override fun lowerTopLevelDeclaration(
        declaration: TopLevelDeclaration,
        owner: NodeOwner<ModuleDeclaration>?
    ): TopLevelDeclaration? {
        return when (declaration) {
            is VariableDeclaration -> declaration.tryLowerToClassDeclaration(owner)
            else -> super.lowerTopLevelDeclaration(declaration, owner)
        }
    }

    private fun VariableDeclaration.tryLowerToClassDeclaration(owner: NodeOwner<ModuleDeclaration>?): TopLevelDeclaration? {
        val topLevelDeclaration = type.getTopLevelDeclaration()
        val constructorSignatureDeclarations = topLevelDeclaration.findAllConstructSignatureDeclarations()

        if (constructorSignatureDeclarations.isEmpty()) {
            return super.lowerVariableDeclaration(this, owner)
        }

        val typeParameters = type.getTypeParameters()
        val appliedTypeParameters = topLevelDeclaration.getAppliedTypeParametersMap(typeParameters)

        val classDeclaration = createClassWith(
            uid = uid,
            name = name,
            owner = owner,
            constructSignatures = constructorSignatureDeclarations.applyTypeParameters(appliedTypeParameters),
            staticallyInheritedFrom = type as? TypeDeclaration
        )

        return super.lowerClassDeclaration(classDeclaration, owner);
    }

    private fun ParameterValueDeclaration.getTopLevelDeclaration(): TopLevelDeclaration? {
        return (this as? TypeDeclaration)?.let { topLevelDeclarationResolver.resolve(it.typeReference) }
    }

    private fun ParameterValueDeclaration.getTypeParameters(): List<ParameterValueDeclaration> {
        return (this as? TypeDeclaration)?.params ?: emptyList()
    }

    private fun TopLevelDeclaration?.findAllConstructSignatureDeclarations(): List<ConstructSignatureDeclaration> {
        return (this as? InterfaceDeclaration)?.let { members.filterIsInstance<ConstructSignatureDeclaration>() }
            ?: emptyList()
    }

    private fun TopLevelDeclaration?.getAppliedTypeParametersMap(typeParameters: List<ParameterValueDeclaration>): TypeMapping {
        val interfaceDeclaration = this as? InterfaceDeclaration ?: return emptyMap()
        return interfaceDeclaration.typeParameters.withIndex().associate { (index, type) ->
            val resolvedType = typeParameters.getOrNull(index)
                ?: type.defaultValue
                // TODO: Handle multiple constraints
                ?: type.constraints.firstOrNull()

            type.name to resolvedType!!
        }
    }

    private fun List<ConstructSignatureDeclaration>.applyTypeParameters(typeMapping: TypeMapping): List<ConstructSignatureDeclaration> {
        val typeParametersResolver = ApplyTypeParameters(typeMapping)
        return map { typeParametersResolver.lowerConstructSignatureDeclaration(it, null) }
    }

    private fun createClassWith(
        name: String,
        uid: String,
        owner: NodeOwner<ModuleDeclaration>?,
        constructSignatures: List<ConstructSignatureDeclaration>,
        staticallyInheritedFrom: TypeDeclaration?
    ): ClassDeclaration {
        // TODO: Handle constructor overloading
        val genericConstructor = constructSignatures.find { it.typeParameters.isNotEmpty() }
        val constructorDeclarations = constructSignatures.map { it.convertToConstructorDeclaration() }
        val parentEntities = constructSignatures.map { it.generateHeritageClauseDeclaration() }
        val staticallyInherited = staticallyInheritedFrom?.generateHeritageClauseDeclaration()

        return ClassDeclaration(
            uid = uid,
            name = IdentifierEntity(name),
            members = constructorDeclarations,
            typeParameters = genericConstructor?.typeParameters ?: emptyList(),
            parentEntities = parentEntities,
            modifiers = setOf(ModifierDeclaration.DECLARE_KEYWORD),
            staticallyInherited = staticallyInherited?.let { listOf(it) } ?: emptyList(),
            definitionsInfo = listOf(DefinitionInfoDeclaration(uid, owner?.node?.sourceName ?: "")),
        )
    }

    private fun ConstructSignatureDeclaration.generateHeritageClauseDeclaration(): HeritageClauseDeclaration {
        return (type as? TypeDeclaration).generateHeritageClauseDeclaration()
    }

    private fun TypeDeclaration?.generateHeritageClauseDeclaration(): HeritageClauseDeclaration {
       return HeritageClauseDeclaration(
           extending = false,
           name = this?.value ?: IdentifierEntity(""),
           typeArguments = this?.params ?: emptyList(),
           typeReference = this?.typeReference
       )
    }

    private fun ConstructSignatureDeclaration.convertToConstructorDeclaration(): ConstructorDeclaration {
        return ConstructorDeclaration(
            parameters = parameters,
            typeParameters = typeParameters,
            modifiers = emptySet(),
            body = null
        )
    }

}

class ReplaceExpressionExtension : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val topLevelDeclarationResolver = TopLevelDeclarationResolver(source)

        return source.copy(sources = source.sources.map {
            it.copy(root = ReplaceExpressionExtensionLowering(topLevelDeclarationResolver).lowerSourceDeclaration(it.root))
        })
    }
}