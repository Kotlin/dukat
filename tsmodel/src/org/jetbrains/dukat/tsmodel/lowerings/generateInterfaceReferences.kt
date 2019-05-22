package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.canBeJson

internal fun Entity.getTypeParams(): List<TypeParameterDeclaration> {
    return when (this) {
        is TypeAliasDeclaration -> typeParameters.map { typeParameter -> TypeParameterDeclaration(typeParameter, emptyList()) }
        is FunctionDeclaration -> typeParameters
        is MethodSignatureDeclaration -> typeParameters
        is ClassDeclaration -> typeParameters
        is InterfaceDeclaration -> typeParameters
        is GeneratedInterfaceDeclaration -> typeParameters
        is VariableDeclaration -> emptyList()
        is PropertyDeclaration -> typeParameters
        else -> raiseConcern("unknown Entity ${this::class.simpleName}") { emptyList<TypeParameterDeclaration>() };
    }
}


private class GenerateInterfaceReferences : DeclarationWithOwnerLowering {

    private val myAstContext: GeneratedInterfacesContext = GeneratedInterfacesContext()

    override fun lowerTypeDeclaration(owner: NodeOwner<TypeDeclaration>) = owner.node

    override fun lowerFunctionTypeDeclaration(owner: NodeOwner<FunctionTypeDeclaration>): FunctionTypeDeclaration {
        val declaration = owner.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameterDeclaration -> parameterDeclaration.copy(type = lowerParameterValue(owner.wrap(parameterDeclaration.type))) }
        )
    }

    override fun lowerIntersectionTypeDeclatation(owner: NodeOwner<IntersectionTypeDeclaration>): IntersectionTypeDeclaration {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(owner.wrap(param)) })
    }

    override fun lowerUnionTypeDeclation(owner: NodeOwner<UnionTypeDeclaration>): UnionTypeDeclaration {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(owner.wrap(param)) })
    }

    override fun lowerObjectDeclaration(owner: NodeOwner<ObjectLiteralDeclaration>): ParameterValueDeclaration {
        val declaration = owner.node
        return when {
            declaration.canBeJson() -> TypeDeclaration(IdentifierEntity("Json"), emptyList())
            declaration.members.isEmpty() -> TypeDeclaration(IdentifierEntity("Any"), emptyList())
            else -> {
                val ownerEntity = owner.topmostEntity()

                val ownerUID = ownerEntity?.getUID() ?: ""

                val ownerTypeParameters = ownerEntity?.getTypeParams() ?: emptyList()
                val parentTypeParams = owner.owner?.node?.getTypeParams() ?: emptyList()
                val typeParameters = ownerTypeParameters + parentTypeParams

                myAstContext.registerObjectLiteralDeclaration(
                        owner.wrap(declaration.copy(members = declaration.members.map { param ->
                            lowerMemberDeclaration(owner.wrap(param))
                        })),
                        ownerUID,
                        typeParameters.map { it.name }.toSet()
                )
            }
        }
    }

    override fun lowerParameterDeclaration(owner: NodeOwner<ParameterDeclaration>) = owner.node

    fun getContext(): GeneratedInterfacesContext {
        return myAstContext
    }

    override fun lowerTypeParameter(owner: NodeOwner<TypeParameterDeclaration>) = owner.node


    // TODO: it looks like we haven't covered interface generation for interface method signatures
    override fun lowerMethodSignatureDeclaration(owner: NodeOwner<MethodSignatureDeclaration>) = owner.node

    override fun lowerInterfaceDeclaration(owner: NodeOwner<InterfaceDeclaration>): InterfaceDeclaration {
        val declaration = owner.node
        return declaration.copy(
            typeParameters = lowerTypeParams(owner, declaration.typeParameters),
            members = declaration.members.map { member -> lowerMemberDeclaration(owner.wrap(member)) }
        )
    }

    override fun lowerClassDeclaration(owner: NodeOwner<ClassDeclaration>): ClassDeclaration {
        val declaration = owner.node
        return declaration.copy(
            typeParameters = lowerTypeParams(owner, declaration.typeParameters),
            members = declaration.members.map { member -> lowerMemberDeclaration(owner.wrap(member)) }
        )
    }


    private fun NodeOwner<*>.topmostEntity(): TopLevelEntity? {
        val topOwner = generateSequence(this) {
            it.owner
        }.lastOrNull { (it.node is TopLevelEntity) && (it.node !is PackageDeclaration) }

        return (topOwner?.node as TopLevelEntity)
    }

    private fun <T: Entity> lowerTypeParams(owner: NodeOwner<T>, typeParams: List<TypeParameterDeclaration>): List<TypeParameterDeclaration> {
        return typeParams.map {typeParam ->
            typeParam.copy(constraints = typeParam.constraints.map { constraint ->  lowerParameterValue(owner.wrap(constraint)) } )
        }
    }

    override fun lowerMemberDeclaration(owner: NodeOwner<MemberEntity>): MemberEntity {
        val declaration = owner.node
        return when (declaration) {
            is IndexSignatureDeclaration -> {
                declaration.copy(returnType = lowerParameterValue(owner.wrap(declaration.returnType)))
            }

            is CallSignatureDeclaration -> {
                declaration.copy(
                        typeParameters = lowerTypeParams(owner, declaration.typeParameters),
                        parameters = declaration.parameters.map { param ->
                            param.copy(type = lowerParameterValue(owner.wrap(param.type)))
                        },
                        type = lowerParameterValue(owner.wrap(declaration.type))
                )
            }
            is ConstructorDeclaration -> {
                declaration.copy(
                        typeParameters = lowerTypeParams(owner, declaration.typeParameters),
                        parameters = declaration.parameters.map { param ->
                            param.copy(type = lowerParameterValue(owner.wrap(param.type)))
                        }
                )

            }
            is PropertyDeclaration -> {
                declaration.copy(
                    typeParameters = lowerTypeParams(owner, declaration.typeParameters),
                    type = lowerParameterValue(owner.wrap(declaration.type))
                )
            }
            is MethodSignatureDeclaration -> {
                declaration.copy(
                        typeParameters = lowerTypeParams(owner, declaration.typeParameters),
                        parameters = declaration.parameters.map { param ->
                            param.copy(type = lowerParameterValue(owner.wrap(param.type)))
                        },
                        type = lowerParameterValue(owner.wrap(declaration.type))
                )
            }
            is FunctionDeclaration -> {
                declaration.copy(
                        typeParameters = lowerTypeParams(owner, declaration.typeParameters),
                        parameters = declaration.parameters.map { param ->
                            param.copy(type = lowerParameterValue(owner.wrap(param.type)))
                        },
                        type = lowerParameterValue(owner.wrap(declaration.type))
                )
            }
            else -> declaration
        }
    }


    override fun lowerTypeAliasDeclaration(owner: NodeOwner<TypeAliasDeclaration>): TypeAliasDeclaration {
        val declaration = owner.node
        return declaration.copy(typeReference = lowerParameterValue(owner.wrap(declaration.typeReference)))
    }

    override fun lowerFunctionDeclaration(owner: NodeOwner<FunctionDeclaration>): FunctionDeclaration {
        val declaration = owner.node
        return declaration.copy(
                typeParameters = lowerTypeParams(owner, declaration.typeParameters),
                parameters = declaration.parameters.map { param ->
                    param.copy(type = lowerParameterValue(owner.wrap(param.type)))
                },
                type = lowerParameterValue(owner.wrap(declaration.type))
        )
    }

    override fun lowerVariableDeclaration(owner: NodeOwner<VariableDeclaration>): VariableDeclaration {
        val declaration = owner.node
        return when (declaration.type) {
            is ObjectLiteralDeclaration -> {
                declaration.copy(type = declaration.type.copy(
                        members = declaration.type.members.map { member -> lowerMemberDeclaration(owner.wrap(member)) }
                ))
            }
            else -> declaration
        }
    }

    override fun lowerDocumentRoot(documentRoot: PackageDeclaration, owner: NodeOwner<PackageDeclaration>): PackageDeclaration {

        val declarations = documentRoot.declarations.map { declaration ->
            when (declaration) {
                !is TypeAliasDeclaration -> lowerTopLevelDeclaration(owner.wrap(declaration))
                else -> declaration
            }
        }.map { declaration ->
            when (declaration) {
                is TypeAliasDeclaration -> lowerTopLevelDeclaration(owner.wrap(declaration))
                else -> declaration
            }
        }

        return documentRoot.copy(declarations = declarations)
    }
}

fun PackageDeclaration.generateInterfaceReferences(): PackageDeclaration {
    val generateInterfaceReferences = GenerateInterfaceReferences()
    return generateInterfaceReferences.getContext().introduceGeneratedEntities(generateInterfaceReferences.lowerDocumentRoot(this, NodeOwner(this, null)))
}

fun SourceFileDeclaration.generateInterfaceReferences() = copy(root = root.generateInterfaceReferences())

fun SourceSetDeclaration.generateInterfaceReferences() = copy(sources = sources.map(SourceFileDeclaration::generateInterfaceReferences))