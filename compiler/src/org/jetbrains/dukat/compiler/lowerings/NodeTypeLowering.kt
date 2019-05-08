package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.ValueTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


interface NodeTypeLowering : NodeLowering {

    fun lowerIdentificator(identificator: NameNode): NameNode {
        return when(identificator) {
            is IdentifierNode -> identificator.copy(value = lowerIdentificator(identificator.value))
            is QualifiedNode -> identificator
            else -> throw Exception("unknown NameNode ${identificator}")
        }
    }

    fun lowerIdentificator(identificator: String): String {
        return identificator;
    }

    fun lowerMethodNode(owner: NodeOwner<MethodNode>): MethodNode {
        val declaration = owner.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                parameters = declaration.parameters.map { parameter -> lowerParameterNode(owner.wrap(parameter)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(owner.wrap(constraint)) })
                },
                type = lowerParameterValue(owner.wrap(declaration.type))
        )
    }

    fun lowerPropertyNode(owner: NodeOwner<PropertyNode>): PropertyNode {
        val declaration = owner.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                type = lowerParameterValue(owner.wrap(declaration.type)),
                typeParameters = declaration.typeParameters.map {
                    typeParameter -> lowerTypeParameter(owner.wrap(typeParameter))
                }
        )
    }

    override fun lowerMemberNode(owner: NodeOwner<MemberNode>): MemberNode {
        val declaration = owner.node
        return when (declaration) {
            is MethodNode -> lowerMethodNode(owner.wrap(declaration))
            is PropertyNode -> lowerPropertyNode(owner.wrap(declaration))
            is ConstructorNode -> lowerConstructorNode(owner.wrap(declaration))
            else -> {
                println("[WARN] [${this::class.simpleName}] skipping ${declaration}")
                declaration
            }
        }
    }

    override fun lowerFunctionNode(owner: NodeOwner<FunctionNode>): FunctionNode {
        val declaration = owner.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                parameters = declaration.parameters.map { parameter -> lowerParameterNode(owner.wrap(parameter)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(owner.wrap(constraint)) })
                },
                type = lowerParameterValue(NodeOwner(declaration.type, owner))
        )
    }

    override fun lowerTypeParameter(owner: NodeOwner<TypeParameterDeclaration>): TypeParameterDeclaration {
        val declaration = owner.node
        return declaration.copy(
            name = lowerIdentificator(declaration.name),
            constraints = declaration.constraints.map { constraint -> lowerParameterValue(NodeOwner(constraint, owner)) }
        )
    }

    override fun lowerUnionTypeDeclaration(owner: NodeOwner<UnionTypeDeclaration>): UnionTypeDeclaration {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(owner.wrap(param))
        })
    }

    override fun lowerTupleDeclaration(owner: NodeOwner<TupleDeclaration>): ParameterValueDeclaration {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(owner.wrap(param))
        })
    }

    override fun lowerUnionTypeNode(owner: NodeOwner<UnionTypeNode>): UnionTypeNode {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(owner.wrap(param))
        })
    }

    override fun lowerIntersectionTypeDeclaration(owner: NodeOwner<IntersectionTypeDeclaration>): IntersectionTypeDeclaration {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(owner.wrap(param))
        })
    }

    override fun lowerTypeNode(owner: NodeOwner<ValueTypeNode>): ValueTypeNode {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(owner.wrap(param))
        })
    }

    override fun lowerFunctionNode(owner: NodeOwner<FunctionTypeNode>): FunctionTypeNode {
        val declaration = owner.node
        return declaration.copy(
                parameters = declaration.parameters.map { param -> lowerParameterNode(owner.wrap(param)) },
                type = lowerParameterValue(owner.wrap(declaration.type))
        )
    }

    override fun lowerParameterNode(owner: NodeOwner<ParameterNode>): ParameterNode {
        val declaration = owner.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                type = lowerParameterValue(owner.wrap(declaration.type))
        )
    }

    override fun lowerVariableNode(owner: NodeOwner<VariableNode>): VariableNode {
        val declaration = owner.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                type = lowerParameterValue(owner.wrap(declaration.type))
        )
    }

    fun lowerHeritageNode(owner: NodeOwner<HeritageNode>): HeritageNode {
        val heritageClause = owner.node
        val typeArguments = heritageClause.typeArguments.map {
            // TODO: obviously very clumsy place
            val typeNode = ValueTypeNode(it.value, emptyList())
            val lowerParameterDeclaration = lowerParameterValue(owner.wrap(typeNode)) as ValueTypeNode
            lowerParameterDeclaration.value as IdentifierNode
        }
        return heritageClause.copy(typeArguments = typeArguments)
    }


    override fun lowerInterfaceNode(owner: NodeOwner<InterfaceNode>): InterfaceNode {

        val declaration = owner.node

        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                members = declaration.members.map { member -> lowerMemberNode(owner.wrap(member)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(owner.wrap(heritageClause))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(owner.wrap(typeParameter))
                }
        )
    }

    override fun lowerTypeAliasNode(owner: NodeOwner<TypeAliasNode>): TypeAliasNode {
        val declaration = owner.node
        return declaration.copy(typeReference = lowerParameterValue(owner.wrap(declaration.typeReference)))
    }

    fun lowerConstructorNode(owner: NodeOwner<ConstructorNode>): ConstructorNode {
        val declaration = owner.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterNode(owner.wrap(parameter)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(owner.wrap(constraint)) })
                }
        )
    }

    override fun lowerObjectNode(owner: NodeOwner<ObjectNode>): ObjectNode {
        val declaration = owner.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberNode(owner.wrap(member)) }
        )

    }

    override fun lowerClassNode(owner: NodeOwner<ClassNode>): ClassNode {
        val declaration = owner.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                members = declaration.members.map { member -> lowerMemberNode(NodeOwner(member, owner)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(owner.wrap(heritageClause))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(owner.wrap(typeParameter))
                }
        )
    }
}