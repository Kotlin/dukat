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
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
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

    fun lowerMethodNode(ownerContext: NodeOwner<MethodNode>): MethodNode {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(ownerContext.wrap(parameter)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(ownerContext.wrap(constraint)) })
                },
                type = lowerParameterValue(ownerContext.wrap(declaration.type))
        )
    }

    fun lowerPropertyNode(ownerContext: NodeOwner<PropertyNode>): PropertyNode {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                type = lowerParameterValue(ownerContext.wrap(declaration.type)),
                typeParameters = declaration.typeParameters.map {
                    typeParameter -> lowerTypeParameter(ownerContext.wrap(typeParameter))
                }
        )
    }

    override fun lowerMemberNode(ownerContext: NodeOwner<MemberNode>): MemberNode {
        val declaration = ownerContext.node
        return when (declaration) {
            is MethodNode -> lowerMethodNode(ownerContext.wrap(declaration))
            is PropertyNode -> lowerPropertyNode(ownerContext.wrap(declaration))
            is ConstructorNode -> lowerConstructorNode(ownerContext.wrap(declaration))
            else -> {
                println("[WARN] [${this::class.simpleName}] skipping ${declaration}")
                declaration
            }
        }
    }

    override fun lowerFunctionNode(ownerContext: NodeOwner<FunctionNode>): FunctionNode {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(ownerContext.wrap(parameter)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(ownerContext.wrap(constraint)) })
                },
                type = lowerParameterValue(NodeOwner(declaration.type, ownerContext))
        )
    }

    override fun lowerTypeParameter(ownerContext: NodeOwner<TypeParameterDeclaration>): TypeParameterDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(
            name = lowerIdentificator(declaration.name),
            constraints = declaration.constraints.map { constraint -> lowerParameterValue(NodeOwner(constraint, ownerContext)) }
        )
    }

    override fun lowerUnionTypeDeclaration(ownerContext: NodeOwner<UnionTypeDeclaration>): UnionTypeDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(ownerContext.wrap(param))
        })
    }

    override fun lowerTupleDeclaration(ownerContext: NodeOwner<TupleDeclaration>): ParameterValueDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(ownerContext.wrap(param))
        })
    }

    override fun lowerUnionTypeNode(ownerContext: NodeOwner<UnionTypeNode>): UnionTypeNode {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(ownerContext.wrap(param))
        })
    }

    override fun lowerIntersectionTypeDeclaration(ownerContext: NodeOwner<IntersectionTypeDeclaration>): IntersectionTypeDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(ownerContext.wrap(param))
        })
    }

    override fun lowerTypeNode(ownerContext: NodeOwner<TypeNode>): TypeNode {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map {
            param -> lowerParameterValue(ownerContext.wrap(param))
        })
    }

    override fun lowerFunctionNode(ownerContext: NodeOwner<FunctionTypeNode>): FunctionTypeNode {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { param -> lowerParameterDeclaration(ownerContext.wrap(param)) },
                type = lowerParameterValue(ownerContext.wrap(declaration.type))
        )
    }

    override fun lowerParameterDeclaration(ownerContext: NodeOwner<ParameterDeclaration>): ParameterDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                type = lowerParameterValue(ownerContext.wrap(declaration.type))
        )
    }

    override fun lowerVariableNode(ownerContext: NodeOwner<VariableNode>): VariableNode {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                type = lowerParameterValue(ownerContext.wrap(declaration.type))
        )
    }

    fun lowerHeritageNode(ownerContext: NodeOwner<HeritageNode>): HeritageNode {
        val heritageClause = ownerContext.node
        val typeArguments = heritageClause.typeArguments.map {
            // TODO: obviously very clumsy place
            val typeNode = TypeNode(it.value, emptyList())
            val lowerParameterDeclaration = lowerParameterValue(ownerContext.wrap(typeNode)) as TypeNode
            lowerParameterDeclaration.value as IdentifierNode
        }
        return heritageClause.copy(typeArguments = typeArguments)
    }


    override fun lowerInterfaceNode(ownerContext: NodeOwner<InterfaceNode>): InterfaceNode {

        val declaration = ownerContext.node

        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                members = declaration.members.map { member -> lowerMemberNode(ownerContext.wrap(member)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(ownerContext.wrap(heritageClause))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(ownerContext.wrap(typeParameter))
                }
        )
    }

    override fun lowerTypeAliasDeclaration(ownerContext: NodeOwner<TypeAliasDeclaration>): TypeAliasDeclaration {
        val declaration = ownerContext.node
        return declaration.copy(typeReference = lowerParameterValue(ownerContext.wrap(declaration.typeReference)))
    }

    fun lowerConstructorNode(ownerContext: NodeOwner<ConstructorNode>): ConstructorNode {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterDeclaration(ownerContext.wrap(parameter)) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> lowerParameterValue(ownerContext.wrap(constraint)) })
                }
        )
    }

    override fun lowerObjectNode(ownerContext: NodeOwner<ObjectNode>): ObjectNode {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberNode(ownerContext.wrap(member)) }
        )

    }

    override fun lowerClassNode(ownerContext: NodeOwner<ClassNode>): ClassNode {
        val declaration = ownerContext.node
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                members = declaration.members.map { member -> lowerMemberNode(NodeOwner(member, ownerContext)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(ownerContext.wrap(heritageClause))
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(ownerContext.wrap(typeParameter))
                }
        )
    }
}