package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.Declaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionOwnerDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private val logger = Logging.logger("TypeLowering")

interface DeclarationTypeLowering : DeclarationLowering {

    fun lowerPropertyDeclaration(declaration: PropertyDeclaration, owner: NodeOwner<MemberEntity>): PropertyDeclaration {
        return declaration.copy(
                type = lowerParameterValue(declaration.type),
                typeParameters = declaration.typeParameters.map { typeParameter -> lowerTypeParameter(typeParameter, owner.wrap(declaration)) }
        )
    }

    fun lowerConstructorDeclaration(declaration: ConstructorDeclaration, owner: NodeOwner<MemberEntity>): ConstructorDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                }
        )
    }

    fun lowerCallSignatureDeclaration(declaration: CallSignatureDeclaration, owner: NodeOwner<MemberEntity>): CallSignatureDeclaration {
        return declaration.copy(
                type = lowerParameterValue(declaration.type),
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                }
        )
    }

    override fun lowerIndexSignatureDeclaration(declaration: IndexSignatureDeclaration, owner: NodeOwner<MemberEntity>): IndexSignatureDeclaration {
        return declaration.copy(
                indexTypes = declaration.indexTypes.map { indexType -> lowerParameterDeclaration(indexType) },
                returnType = lowerParameterValue(declaration.returnType)
        );
    }

    @Suppress("UNCHECKED_CAST")
    override fun lowerMemberDeclaration(declaration: MemberEntity, owner: NodeOwner<ClassLikeDeclaration>): MemberEntity {
        val newOwner = owner.wrap(declaration)
        return when (declaration) {
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration, newOwner as NodeOwner<FunctionOwnerDeclaration>)
            is PropertyDeclaration -> lowerPropertyDeclaration(declaration, newOwner)
            is ConstructorDeclaration -> lowerConstructorDeclaration(declaration, newOwner)
            is MethodSignatureDeclaration -> lowerMethodSignatureDeclaration(declaration, newOwner)
            is CallSignatureDeclaration -> lowerCallSignatureDeclaration(declaration, newOwner)
            is IndexSignatureDeclaration -> lowerIndexSignatureDeclaration(declaration, newOwner)
            else -> {
                logger.debug("[${this}] skipping ${declaration}")
                declaration
            }
        }
    }

    override fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration, owner: NodeOwner<MemberEntity>): MethodSignatureDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                },
                type = lowerParameterValue(declaration.type)
        )
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration, owner: NodeOwner<FunctionOwnerDeclaration>): FunctionDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                },
                type = lowerParameterValue(declaration.type)
        )
    }

    override fun lowerTypeParameter(declaration: TypeParameterDeclaration, owner: NodeOwner<Declaration>): TypeParameterDeclaration {
        return declaration.copy(constraints = declaration.constraints.map { constraint -> lowerParameterValue(constraint) })
    }

    override fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration): UnionTypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
    }

    override fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration): IntersectionTypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
    }

    override fun lowerTupleDeclaration(declaration: TupleDeclaration): TupleDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
    }

    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
    }

    override fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration): FunctionTypeDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { param -> lowerParameterDeclaration(param) },
                type = lowerParameterValue(declaration.type)
        )
    }

    override fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration {
        return declaration.copy(type = lowerParameterValue(declaration.type))
    }

    override fun lowerVariableDeclaration(declaration: VariableDeclaration, owner: NodeOwner<ModuleDeclaration>): VariableDeclaration {
        return declaration.copy(type = lowerParameterValue(declaration.type))
    }

    fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration, owner: NodeOwner<ClassLikeDeclaration>): HeritageClauseDeclaration {
        return heritageClause.copy(typeArguments = heritageClause.typeArguments.map { typeArgument -> lowerParameterValue(typeArgument) })
    }


    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>): InterfaceDeclaration {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(member, owner.wrap(declaration)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(heritageClause, owner.wrap(declaration))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter, owner.wrap(declaration))
                }
        )
    }

    override fun lowerGeneratedInterfaceDeclaration(declaration: GeneratedInterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>): GeneratedInterfaceDeclaration {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(member, owner.wrap(declaration)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(heritageClause, owner.wrap(declaration))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter, owner.wrap(declaration))
                }
        )

    }

    override fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration, owner: NodeOwner<ModuleDeclaration>): TypeAliasDeclaration {
        return declaration.copy(typeReference = lowerParameterValue(declaration.typeReference))
    }

    override fun lowerClassDeclaration(declaration: ClassDeclaration, owner: NodeOwner<ModuleDeclaration>): ClassDeclaration {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(member, owner.wrap(declaration)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(heritageClause, owner.wrap(declaration))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter, owner.wrap(declaration))
                }
        )
    }
}