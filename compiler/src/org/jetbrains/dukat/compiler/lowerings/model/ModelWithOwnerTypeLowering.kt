package org.jetbrains.dukat.compiler.lowerings.model

import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.ValueTypeNode
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration

interface ModelWithOwnerTypeLowering : ModelWithOwnerLowering {

    override fun lowerEnumNode(ownerContext: NodeOwner<EnumNode>): EnumNode {
        return ownerContext.node
    }

    fun lowerMethodNode(ownerContext: NodeOwner<MethodNode>): MethodNode {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(NodeOwner(parameter, ownerContext)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(NodeOwner(constraint, ownerContext)) })
                },
                type = lowerParameterValue(NodeOwner(declaration.type, ownerContext))
        )
    }

    fun lowerPropertyNode(ownerContext: NodeOwner<PropertyNode>): PropertyNode {
        val declaration = ownerContext.node
        return declaration.copy(
                type = lowerParameterValue(NodeOwner(declaration.type, ownerContext)),
                typeParameters = declaration.typeParameters.map { typeParameter -> lowerTypeParameter(NodeOwner(typeParameter, ownerContext)) }
        )
    }

    override fun lowerMemberNode(ownerContext: NodeOwner<MemberNode>): MemberNode {
        val declaration = ownerContext.node
        return when (declaration) {
            is MethodNode -> lowerMethodNode(NodeOwner(declaration, ownerContext))
            is PropertyNode -> lowerPropertyNode(NodeOwner(declaration, ownerContext))
            is ConstructorNode -> lowerConstructorNode(NodeOwner(declaration, ownerContext))
            else -> {
                println("[WARN] [${this::class.simpleName}] skipping ${declaration}")
                declaration
            }
        }
    }

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>): FunctionModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(NodeOwner(parameter, ownerContext)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(NodeOwner(constraint, ownerContext)) })
                },
                type = lowerParameterValue(NodeOwner(declaration.type, ownerContext))
        )
    }

    override fun lowerTypeParameter(ownerContext: NodeOwner<TypeParameterDeclaration>): TypeParameterDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(constraints = declaration.constraints.map { constraint ->
            val lowerParameterValue = lowerParameterValue(NodeOwner(constraint, ownerContext))
            lowerParameterValue
        })
    }

    override fun lowerTupleDeclaration(ownerContext: NodeOwner<TupleDeclaration>): ParameterValueDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(NodeOwner(param, ownerContext)) })
    }

    override fun lowerUnionTypeNode(ownerContext: NodeOwner<UnionTypeNode>): UnionTypeNode {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(NodeOwner(param, ownerContext)) })
    }

    override fun lowerTypeNode(ownerContext: NodeOwner<ValueTypeNode>): ValueTypeNode {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(NodeOwner(param, ownerContext)) })
    }

    override fun lowerFunctionTypeNode(ownerContext: NodeOwner<FunctionTypeNode>): FunctionTypeNode {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { param -> lowerParameterDeclaration(NodeOwner(param, ownerContext)) },
                type = lowerParameterValue(NodeOwner(declaration.type, ownerContext))
        )
    }

    override fun lowerParameterDeclaration(ownerContext: NodeOwner<ParameterDeclaration>): ParameterDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(type = lowerParameterValue(NodeOwner(declaration.type, ownerContext)))
    }

    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>): VariableModel {
        val declaration = ownerContext.node
        return declaration.copy(type = lowerParameterValue(NodeOwner(declaration.type, ownerContext)))
    }

    fun lowerHeritageNode(ownerContext: NodeOwner<HeritageNode>): HeritageNode {
        val heritageClause = ownerContext.node
        val typeArguments = heritageClause.typeArguments.map {
            // TODO: obviously very clumsy place
            val lowerParameterDeclaration = lowerParameterValue(NodeOwner(ValueTypeNode(it.value, emptyList()), ownerContext)) as ValueTypeNode
            lowerParameterDeclaration.value as IdentifierNode
        }
        return heritageClause.copy(typeArguments = typeArguments)
    }


    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>): InterfaceModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members
                = declaration.members.map { member -> lowerMemberNode(NodeOwner(member, ownerContext)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(NodeOwner(heritageClause, ownerContext))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(NodeOwner(typeParameter, ownerContext))
                }
        )
    }


    fun lowerConstructorNode(ownerContext: NodeOwner<ConstructorNode>): ConstructorNode {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(NodeOwner(parameter, ownerContext)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(NodeOwner(constraint, ownerContext)) })
                }
        )
    }

    override fun lowerObjectModel(ownerContext: NodeOwner<ObjectModel>): ObjectModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberNode(NodeOwner(member, ownerContext)) }
        )

    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberNode(NodeOwner(member, ownerContext)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(NodeOwner(heritageClause, ownerContext))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(NodeOwner(typeParameter, ownerContext))
                }
        )
    }
}
