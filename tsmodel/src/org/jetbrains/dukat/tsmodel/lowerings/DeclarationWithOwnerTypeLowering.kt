package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


interface DeclarationWithOwnerTypeLowering : DeclarationWithOwnerLowering {

    fun lowerPropertyDeclaration(owner: NodeOwner<PropertyDeclaration>): PropertyDeclaration {
        val declaration = owner.node
        return declaration.copy(
                type = lowerParameterValue(owner.wrap(declaration.type)),
                typeParameters = declaration.typeParameters.map { typeParameter -> lowerTypeParameter(owner.wrap(typeParameter)) }
        )
    }

    fun lowerConstructorDeclaration(owner: NodeOwner<ConstructorDeclaration>): ConstructorDeclaration {
        val declaration = owner.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(owner.wrap(parameter)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(owner.wrap(constraint)) })
                }
        )
    }

    fun lowerCallSignatureDeclaration(owner: NodeOwner<CallSignatureDeclaration>): CallSignatureDeclaration {
        val declaration = owner.node
        return declaration.copy(
                type = lowerParameterValue(owner.wrap(declaration.type)),
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(owner.wrap(parameter)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(owner.wrap(constraint)) })
                }
        )
    }


    override fun lowerMemberDeclaration(owner: NodeOwner<MemberEntity>): MemberEntity {
        val declaration = owner.node
        return when (declaration) {
            is FunctionDeclaration -> lowerFunctionDeclaration(owner.wrap(declaration))
            is PropertyDeclaration -> lowerPropertyDeclaration(owner.wrap(declaration))
            is ConstructorDeclaration -> lowerConstructorDeclaration(owner.wrap(declaration))
            is MethodSignatureDeclaration -> lowerMethodSignatureDeclaration(owner.wrap(declaration))
            is CallSignatureDeclaration -> lowerCallSignatureDeclaration(owner.wrap(declaration))
            else -> {
                println("[WARN] [${this::class.simpleName}] skipping ${declaration}")
                declaration
            }
        }
    }

    override fun lowerMethodSignatureDeclaration(owner: NodeOwner<MethodSignatureDeclaration>): MethodSignatureDeclaration {
        val declaration = owner.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(owner.wrap(parameter)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(owner.wrap(constraint)) })
                },
                type = lowerParameterValue(owner.wrap(declaration.type))
        )
    }

    override fun lowerFunctionDeclaration(owner: NodeOwner<FunctionDeclaration>): FunctionDeclaration {
        val declaration = owner.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(owner.wrap(parameter)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(owner.wrap(constraint)) })
                },
                type = lowerParameterValue(owner.wrap(declaration.type))
        )
    }

    override fun lowerTypeParameter(owner: NodeOwner<TypeParameterDeclaration>): TypeParameterDeclaration {
        val declaration = owner.node
        return declaration.copy(constraints = declaration.constraints.map { constraint -> lowerParameterValue(owner.wrap(constraint)) })
    }

    override fun lowerUnionTypeDeclation(owner: NodeOwner<UnionTypeDeclaration>): UnionTypeDeclaration {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(owner.wrap(param)) })
    }

    override fun lowerIntersectionTypeDeclatation(owner: NodeOwner<IntersectionTypeDeclaration>): IntersectionTypeDeclaration {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(owner.wrap(param)) })
    }

    override fun lowerTypeDeclaration(owner: NodeOwner<TypeDeclaration>): TypeDeclaration {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(owner.wrap(param)) })
    }

    override fun lowerFunctionTypeDeclaration(owner: NodeOwner<FunctionTypeDeclaration>): FunctionTypeDeclaration {
        val declaration = owner.node
        return declaration.copy(
                parameters = declaration.parameters.map { param -> lowerParameterDeclaration(owner.wrap(param)) },
                type = lowerParameterValue(owner.wrap(declaration.type))
        )
    }

    override fun lowerParameterDeclaration(owner: NodeOwner<ParameterDeclaration>): ParameterDeclaration {
        val declaration = owner.node
        return declaration.copy(type = lowerParameterValue(owner.wrap(declaration.type)))
    }

    override fun lowerVariableDeclaration(owner: NodeOwner<VariableDeclaration>): VariableDeclaration {
        val declaration = owner.node
        return declaration.copy(type = lowerParameterValue(owner.wrap(declaration.type)))
    }

    fun lowerHeritageClause(owner: NodeOwner<HeritageClauseDeclaration>): HeritageClauseDeclaration {
        return owner.node
    }

    override fun lowerInterfaceDeclaration(owner: NodeOwner<InterfaceDeclaration>): InterfaceDeclaration {
        val declaration = owner.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(owner.wrap(member)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(owner.wrap(heritageClause))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(owner.wrap(typeParameter))
                }
        )
    }

    override fun lowerTypeAliasDeclaration(owner: NodeOwner<TypeAliasDeclaration>): TypeAliasDeclaration {
        val declaration = owner.node
        return declaration.copy(typeReference = lowerParameterValue(owner.wrap(declaration.typeReference)))
    }

    override fun lowerClassDeclaration(owner: NodeOwner<ClassDeclaration>): ClassDeclaration {
        val declaration = owner.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(owner.wrap(member)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(owner.wrap(heritageClause))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(owner.wrap(typeParameter))
                }
        )
    }
}