package org.jetbrains.dukat.tsLowerings

import TopLevelDeclarationLowering
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
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MemberOwnerDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
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
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private val logger = Logging.logger("TypeLowering")

interface DeclarationLowering: TopLevelDeclarationLowering  {

    fun lowerPropertyDeclaration(declaration: PropertyDeclaration, owner: NodeOwner<MemberDeclaration>?): PropertyDeclaration {
        return declaration.copy(
                type = lowerParameterValue(declaration.type, owner?.wrap(declaration)),
                typeParameters = declaration.typeParameters.map { typeParameter -> lowerTypeParameter(typeParameter, owner?.wrap(declaration)) }
        )
    }

    fun lowerConstructorDeclaration(declaration: ConstructorDeclaration, owner: NodeOwner<MemberDeclaration>?): ConstructorDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter, owner?.wrap(declaration)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint, owner?.wrap(declaration)) })
                }
        )
    }

    fun lowerCallSignatureDeclaration(declaration: CallSignatureDeclaration, owner: NodeOwner<MemberDeclaration>?): CallSignatureDeclaration {
        return declaration.copy(
                type = lowerParameterValue(declaration.type, owner?.wrap(declaration)),
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter, owner?.wrap(declaration)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint, owner?.wrap(declaration)) })
                }
        )
    }

    fun lowerIndexSignatureDeclaration(declaration: IndexSignatureDeclaration, owner: NodeOwner<MemberDeclaration>?): IndexSignatureDeclaration {
        return declaration.copy(
                indexTypes = declaration.indexTypes.map { indexType -> lowerParameterDeclaration(indexType, owner?.wrap(declaration)) },
                returnType = lowerParameterValue(declaration.returnType, owner?.wrap(declaration))
        );
    }

    @Suppress("UNCHECKED_CAST")
    fun lowerMemberDeclaration(declaration: MemberDeclaration, owner: NodeOwner<MemberOwnerDeclaration>?): MemberDeclaration {
        val newOwner = owner?.wrap(declaration)
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

    fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration, owner: NodeOwner<MemberDeclaration>?): MethodSignatureDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter, owner?.wrap(declaration)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint, owner?.wrap(declaration)) })
                },
                type = lowerParameterValue(declaration.type, owner?.wrap(declaration))
        )
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration, owner: NodeOwner<FunctionOwnerDeclaration>?): FunctionDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter, owner?.wrap(declaration)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint, owner?.wrap(declaration)) })
                },
                type = lowerParameterValue(declaration.type, owner?.wrap(declaration))
        )
    }

    fun lowerTypeParameter(declaration: TypeParameterDeclaration, owner: NodeOwner<Declaration>?): TypeParameterDeclaration {
        return declaration.copy(constraints = declaration.constraints.map { constraint -> lowerParameterValue(constraint, owner?.wrap(declaration)) })
    }

    fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): UnionTypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param, owner?.wrap(declaration)) })
    }

    fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): IntersectionTypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param, owner?.wrap(declaration)) })
    }

    fun lowerTupleDeclaration(declaration: TupleDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TupleDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param, owner?.wrap(declaration)) })
    }

    fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param, owner?.wrap(declaration)) })
    }

    fun lowerObjectLiteralDeclaration(declaration: ObjectLiteralDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ObjectLiteralDeclaration {
        return declaration.copy(members = declaration.members.map { member -> lowerMemberDeclaration(member, owner?.wrap(declaration)) })
    }

    fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): FunctionTypeDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { param -> lowerParameterDeclaration(param, owner?.wrap(declaration)) },
                type = lowerParameterValue(declaration.type, owner?.wrap(declaration))
        )
    }

    fun lowerParameterDeclaration(declaration: ParameterDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterDeclaration {
        return declaration.copy(type = lowerParameterValue(declaration.type, owner))
    }

    override fun lowerVariableDeclaration(declaration: VariableDeclaration, owner: NodeOwner<ModuleDeclaration>?): VariableDeclaration {
        return declaration.copy(type = lowerParameterValue(declaration.type, owner?.wrap(declaration)))
    }

    fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration, owner: NodeOwner<ClassLikeDeclaration>?): HeritageClauseDeclaration {
        return heritageClause.copy(typeArguments = heritageClause.typeArguments.map { typeArgument -> lowerParameterValue(typeArgument, owner?.wrap(heritageClause)) })
    }


    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>?): InterfaceDeclaration {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(member, owner?.wrap(declaration)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(heritageClause, owner?.wrap(declaration))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter, owner?.wrap(declaration))
                }
        )
    }

    override fun lowerGeneratedInterfaceDeclaration(declaration: GeneratedInterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>?): GeneratedInterfaceDeclaration {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(member, owner?.wrap(declaration)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(heritageClause, owner?.wrap(declaration))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter, owner?.wrap(declaration))
                }
        )

    }

    override fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration, owner: NodeOwner<ModuleDeclaration>?): TypeAliasDeclaration {
        return declaration.copy(typeReference = lowerParameterValue(declaration.typeReference, owner?.wrap(declaration)))
    }

    override fun lowerClassDeclaration(declaration: ClassDeclaration, owner: NodeOwner<ModuleDeclaration>?): ClassDeclaration {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(member, owner?.wrap(declaration)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(heritageClause, owner?.wrap(declaration))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter, owner?.wrap(declaration))
                }
        )
    }

    fun lowerTypeParamReferenceDeclaration(declaration: TypeParamReferenceDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterValueDeclaration = declaration

    fun lowerParameterValue(declaration: ParameterValueDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterValueDeclaration {
        return when (declaration) {
            is TypeDeclaration -> lowerTypeDeclaration(declaration, owner)
            is FunctionTypeDeclaration -> lowerFunctionTypeDeclaration(declaration, owner)
            is UnionTypeDeclaration -> lowerUnionTypeDeclaration(declaration, owner)
            is TypeParamReferenceDeclaration -> lowerTypeParamReferenceDeclaration(declaration, owner)
            is TupleDeclaration -> lowerTupleDeclaration(declaration, owner)
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclaration(declaration, owner)
            is ObjectLiteralDeclaration -> lowerObjectLiteralDeclaration(declaration, owner)
            else -> declaration
        }
    }

}