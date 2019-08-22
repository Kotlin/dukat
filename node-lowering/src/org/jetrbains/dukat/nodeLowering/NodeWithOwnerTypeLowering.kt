package org.jetrbains.dukat.nodeLowering

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private val logger = Logging.logger("NodeWithOwnerLowering")

interface NodeWithOwnerTypeLowering : NodeWithOwnerLowering<ParameterValueDeclaration> {

    fun lowerIdentificator(identificator: String): String {
        return identificator;
    }

    fun lowerIdentificator(identificator: NameEntity): NameEntity {
        return when (identificator) {
            is IdentifierEntity -> identificator.copy(value = lowerIdentificator(identificator.value))
            is QualifierEntity -> identificator
            else -> raiseConcern("unknown NameEntity ${identificator}") { identificator }
        }
    }

    fun lowerParameterValue(owner: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {
        return when (val declaration = owner.node) {
            is TypeValueNode -> lowerTypeNode(owner.wrap(declaration))
            is FunctionTypeNode -> lowerFunctionTypeNode(owner.wrap(declaration))
            is UnionTypeNode -> lowerUnionTypeNode(owner.wrap(declaration))
            else -> declaration
        }
    }

    fun lowerMethodNode(owner: NodeOwner<MethodNode>): MethodNode {
        val declaration = owner.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                parameters = declaration.parameters.map { parameter -> lowerParameterNode(owner.wrap(parameter)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(params = typeParameter.params.map { param -> lowerParameterValue(owner.wrap(param)) })
                },
                type = lowerParameterValue(owner.wrap(declaration.type))
        )
    }

    fun lowerPropertyNode(owner: NodeOwner<PropertyNode>): PropertyNode {
        val declaration = owner.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                type = lowerParameterValue(owner.wrap(declaration.type)),
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(owner.wrap(typeParameter))
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
                logger.debug("[${this}] skipping ${declaration}")
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
                    typeParameter.copy(params = typeParameter.params.map { param -> lowerParameterValue(owner.wrap(param)) })
                },
                type = lowerParameterValue(NodeOwner(declaration.type, owner))
        )
    }

    override fun lowerTypeParameter(owner: NodeOwner<TypeValueNode>): TypeValueNode {
        val declaration = owner.node
        return declaration.copy(
                value = lowerIdentificator(declaration.value),
                params = declaration.params.map { param -> lowerParameterValue(NodeOwner(param, owner)) }
        )
    }

    override fun lowerUnionTypeNode(owner: NodeOwner<UnionTypeNode>): UnionTypeNode {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map { param ->
            lowerParameterValue(owner.wrap(param))
        })
    }

    override fun lowerTypeNode(owner: NodeOwner<TypeValueNode>): TypeValueNode {
        val declaration = owner.node
        return declaration.copy(params = declaration.params.map { param ->
            lowerParameterValue(owner.wrap(param))
        })
    }

    override fun lowerFunctionTypeNode(owner: NodeOwner<FunctionTypeNode>): FunctionTypeNode {
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
        val typeArguments = heritageClause.typeArguments.map { typeArgument ->
            // TODO: obviously very clumsy place
            lowerParameterValue(owner.wrap(typeArgument))
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
                    typeParameter.copy(params = typeParameter.params.map { param -> lowerParameterValue(owner.wrap(param)) })
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
                    lowerTypeNode(owner.wrap(typeParameter))
                }
        )
    }
}