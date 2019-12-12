package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
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
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.canBeJson

internal fun Entity.getTypeParams(): List<TypeParameterDeclaration> {
    return when (this) {
        is CallSignatureDeclaration -> typeParameters
        is ClassDeclaration -> typeParameters
        is ConstructorDeclaration -> typeParameters
        is FunctionDeclaration -> typeParameters
        is FunctionTypeDeclaration -> emptyList()
        is GeneratedInterfaceDeclaration -> typeParameters
        is IndexSignatureDeclaration -> emptyList()
        is IntersectionTypeDeclaration -> emptyList()
        is InterfaceDeclaration -> typeParameters
        is MethodSignatureDeclaration -> typeParameters
        is PropertyDeclaration -> typeParameters
        is TupleDeclaration -> emptyList()
        is TypeDeclaration -> emptyList()
        is TypeAliasDeclaration -> typeParameters.map { typeParameter -> TypeParameterDeclaration(typeParameter, emptyList(), null) }
        is UnionTypeDeclaration -> emptyList()
        is VariableDeclaration -> emptyList()
        else -> raiseConcern("unknown Entity ${this}") { emptyList<TypeParameterDeclaration>() };
    }
}

private class GenerateInterfaceReferences : DeclarationWithOwnerLowering {

    private val myAstContext: GeneratedInterfacesContext = GeneratedInterfacesContext()

    override fun lowerTypeDeclaration(owner: NodeOwner<TypeDeclaration>): TypeDeclaration {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(owner.wrap(param)) })
    }

    override fun lowerFunctionTypeDeclaration(owner: NodeOwner<FunctionTypeDeclaration>): FunctionTypeDeclaration {
        val declaration = owner.node
        return declaration.copy(
                type = lowerParameterValue(owner.wrap(declaration.type)),
                parameters = declaration.parameters.map { parameterDeclaration -> parameterDeclaration.copy(type = lowerParameterValue(owner.wrap(parameterDeclaration.type))) }
        )
    }

    override fun lowerIntersectionTypeDeclaration(owner: NodeOwner<IntersectionTypeDeclaration>): IntersectionTypeDeclaration {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(owner.wrap(param)) })
    }

    override fun lowerUnionTypeDeclaration(owner: NodeOwner<UnionTypeDeclaration>): UnionTypeDeclaration {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(owner.wrap(param)) })
    }

    override fun lowerTupleDeclaration(owner: NodeOwner<TupleDeclaration>): TupleDeclaration {
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

                myAstContext.registerObjectLiteralDeclaration(
                        owner.wrap(declaration.copy(members = declaration.members.map { param ->
                            lowerMemberDeclaration(owner.wrap(param))
                        })),
                        ownerUID
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
        }.lastOrNull { (it.node is TopLevelEntity) && (it.node !is ModuleDeclaration) }

        return (topOwner?.node as? TopLevelEntity)
    }

    private fun <T : Entity> lowerTypeParams(owner: NodeOwner<T>, typeParams: List<TypeParameterDeclaration>): List<TypeParameterDeclaration> {
        return typeParams.map { typeParam ->
            typeParam.copy(constraints = typeParam.constraints.map { constraint -> lowerParameterValue(owner.wrap(constraint)) })
        }
    }

    override fun lowerMemberDeclaration(owner: NodeOwner<MemberDeclaration>): MemberDeclaration {
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
        val type = declaration.type
        return when (type) {
            is ObjectLiteralDeclaration -> {
                declaration.copy(type = type.copy(
                        members = type.members.map { member -> lowerMemberDeclaration(owner.wrap(member)) }
                ))
            }
            else -> declaration.copy(type = lowerParameterValue(owner.wrap(type)))
        }
    }

    override fun lowerDocumentRoot(documentRoot: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>): ModuleDeclaration {

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

private fun ModuleDeclaration.generateInterfaceReferences(generateInterfaceReferences: GenerateInterfaceReferences): ModuleDeclaration {
    return generateInterfaceReferences.getContext().introduceGeneratedEntities(generateInterfaceReferences.lowerDocumentRoot(this, NodeOwner(this, null)))
}

private fun SourceFileDeclaration.generateInterfaceReferences(generateInterfaceReferences: GenerateInterfaceReferences): SourceFileDeclaration {
    return copy(root = root.generateInterfaceReferences(generateInterfaceReferences))
}

fun SourceSetDeclaration.generateInterfaceReferences(): SourceSetDeclaration {
    val generateInterfaceReferences = GenerateInterfaceReferences()
    return copy(sources = sources.map { it.generateInterfaceReferences(generateInterfaceReferences) })
}