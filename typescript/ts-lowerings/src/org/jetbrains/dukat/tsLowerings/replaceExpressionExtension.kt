package org.jetbrains.dukat.tsLowerings

import TopLevelDeclarationLowering
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.*
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class ReplaceExpressionExtensionLowering(private val topLevelDeclarationResolver: TopLevelDeclarationResolver) :
    TopLevelDeclarationLowering {
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
        val typeDeclaration = type.getTopLevelDeclaration()
        val constructorSignatureDeclarations = typeDeclaration.findAllConstructSignatureDeclarations()

        if (constructorSignatureDeclarations.isEmpty()) {
            return super.lowerVariableDeclaration(this, owner)
        }

        val classDeclaration = createClassWith(
            uid = uid,
            name = name,
            owner = owner,
            staticallyInheritedFrom = type as? TypeDeclaration,
            constructSignatures = constructorSignatureDeclarations.replaceTypeVariablesWith(
                exactTypes = type.getTypeParameters(),
                typeDeclaration = typeDeclaration
            ),
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

    private fun List<ConstructSignatureDeclaration>.replaceTypeVariablesWith(
        exactTypes: List<ParameterValueDeclaration>,
        typeDeclaration: TopLevelDeclaration?
    ): List<ConstructSignatureDeclaration> {
        val appliedTypeParameters = typeDeclaration.getAppliedTypeParametersMap(exactTypes)
        val typeParametersResolver = ApplyTypeParameters(appliedTypeParameters)
        return map { typeParametersResolver.lowerConstructSignatureDeclaration(it, null) }
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

        val overriddenInstanceMembers = parentEntities.flatMap { it.getOverriddenMembers() }
        val overriddenStaticMembers = staticallyInherited?.getOverriddenMembers()?.map { it.asStatic() } ?: emptyList()

        return ClassDeclaration(
            uid = uid,
            name = IdentifierEntity(name),
            parentEntities = parentEntities,
            modifiers = setOf(ModifierDeclaration.DECLARE_KEYWORD),
            typeParameters = genericConstructor?.typeParameters ?: emptyList(),
            staticallyInherited = staticallyInherited?.let { listOf(it) } ?: emptyList(),
            members = constructorDeclarations + overriddenInstanceMembers + overriddenStaticMembers,
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

    private fun HeritageClauseDeclaration.getOverriddenMembers(): List<MemberDeclaration> {
        val typeDeclaration =
            topLevelDeclarationResolver.resolve(typeReference) as? ClassLikeDeclaration ?: return emptyList()
        val members =
            typeDeclaration.members.filter { it !is ConstructSignatureDeclaration && it !is ConstructorDeclaration }
        return members + typeDeclaration.parentEntities.flatMap { it.getOverriddenMembers() }
    }

    private fun MemberDeclaration.asStatic(): MemberDeclaration {
        return when (this) {
            is MethodSignatureDeclaration -> copy(modifiers = modifiers + ModifierDeclaration.STATIC_KEYWORD)
            is MethodDeclaration -> copy(modifiers = modifiers + ModifierDeclaration.STATIC_KEYWORD)
            is PropertyDeclaration -> copy(modifiers = modifiers + ModifierDeclaration.STATIC_KEYWORD)
            else -> this
        }
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