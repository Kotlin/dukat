package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TokenDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


interface ParameterValueLowering : Lowering {

    fun lowerMethodNode(declaration: MethodNode): MethodNode {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                },
                type = lowerParameterValue(declaration.type)
        )
    }

    fun lowerPropertyNode(declaration: PropertyNode): PropertyNode {
        return declaration.copy(
                type = lowerParameterValue(declaration.type),
                typeParameters = declaration.typeParameters.map { typeParameter -> lowerTypeParameter(typeParameter) }
        )
    }

    override fun lowerMemberNode(declaration: MemberNode): MemberNode {
        return when (declaration) {
            is MethodNode -> lowerMethodNode(declaration)
            is PropertyNode -> lowerPropertyNode(declaration)
            is ConstructorNode -> lowerConstructorNode(declaration)
            else -> {
                println("[WARN] skipping ${declaration}")
                declaration
            }
        }
    }

    override fun lowerFunctionNode(declaration: FunctionNode): FunctionNode {
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

    override fun lowerUnionTypeNode(declaration: UnionTypeNode): UnionTypeNode {
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
    }

    override fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration): IntersectionTypeDeclaration {
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

    override fun lowerVariableNode(declaration: VariableNode): VariableNode {
        return declaration.copy(type = lowerParameterValue(declaration.type))
    }

    fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration): HeritageClauseDeclaration {
        val typeArguments = heritageClause.typeArguments.map {
            val lowerParameterDeclaration = lowerParameterValue(TypeDeclaration(it.value, emptyList())) as TypeDeclaration
            TokenDeclaration(lowerParameterDeclaration.value)
        }
        return heritageClause.copy(typeArguments = typeArguments)
    }


    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {

        return declaration.copy(
                members
                = declaration.members.map { member -> lowerMemberNode(member) },
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

    fun lowerConstructorNode(declaration: ConstructorNode): ConstructorNode {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(constraint) })
                }
        )
    }

    override fun lowerObjectNode(declaration: ObjectNode): ObjectNode {
        return declaration.copy(
            members = declaration.members.map { member -> lowerMemberNode(member) }
        )

    }

    override fun lowerClassNode(declaration: ClassNode): ClassNode {
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberNode(member) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageClause(heritageClause)
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter)
                }
        )
    }
}