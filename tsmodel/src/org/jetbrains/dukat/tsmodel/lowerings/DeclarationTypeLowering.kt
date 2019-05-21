package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
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


interface DeclarationTypeLowering : DeclarationLowering {

    fun lowerPropertyDeclaration(declaration: PropertyDeclaration): PropertyDeclaration {
        return declaration.copy(
                type = lowerParameterValue(declaration.type),
                typeParameters = declaration.typeParameters.map { typeParameter -> lowerTypeParameter(typeParameter) }
        )
    }

    fun lowerConstructorDeclaration(declaration: ConstructorDeclaration): ConstructorDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                }
        )
    }

    fun lowerCallSignatureDeclaration(declaration: CallSignatureDeclaration): CallSignatureDeclaration {
        return declaration.copy(
                type = lowerParameterValue(declaration.type),
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                }
        )
    }

    override fun lowerIndexSignatureDeclaration(declaration: IndexSignatureDeclaration): IndexSignatureDeclaration {
        return declaration.copy(
                indexTypes = declaration.indexTypes.map { indexType -> lowerParameterDeclaration(indexType) },
                returnType = lowerParameterValue(declaration.returnType)
        );
    }

    override fun lowerMemberDeclaration(declaration: MemberEntity): MemberEntity {
        return when (declaration) {
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration)
            is PropertyDeclaration -> lowerPropertyDeclaration(declaration)
            is ConstructorDeclaration -> lowerConstructorDeclaration(declaration)
            is MethodSignatureDeclaration -> lowerMethodSignatureDeclaration(declaration)
            is CallSignatureDeclaration -> lowerCallSignatureDeclaration(declaration)
            is IndexSignatureDeclaration -> lowerIndexSignatureDeclaration(declaration)
            else -> {
                println("[WARN] [${this::class.simpleName}] skipping ${declaration}")
                declaration
            }
        }
    }

    override fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration): MethodSignatureDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                },
                type = lowerParameterValue(declaration.type)
        )
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                },
                type = lowerParameterValue(declaration.type)
        )
    }

    override fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration {
        return declaration.copy(constraints = declaration.constraints.map { constraint -> lowerParameterValue(constraint) })
    }

    override fun lowerUnionTypeDeclation(declaration: UnionTypeDeclaration): UnionTypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
    }

    override fun lowerIntersectionTypeDeclatation(declaration: IntersectionTypeDeclaration): IntersectionTypeDeclaration {
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

    override fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        return declaration.copy(type = lowerParameterValue(declaration.type))
    }

    fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration): HeritageClauseDeclaration {
        return heritageClause
    }


    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(member) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(heritageClause)
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter)
                }
        )
    }

    override fun lowerGeneratedInterfaceDeclaration(declaration: GeneratedInterfaceDeclaration): GeneratedInterfaceDeclaration {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(member) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(heritageClause)
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter)
                }
        )

    }

    override fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration): TypeAliasDeclaration {
        return declaration.copy(typeReference = lowerParameterValue(declaration.typeReference))
    }

    override fun lowerClassDeclaration(declaration: ClassDeclaration): ClassDeclaration {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberDeclaration(member) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(heritageClause)
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter)
                }
        )
    }
}