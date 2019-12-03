package org.jetrbains.dukat.nodeLowering

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
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
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private val logger = Logging.logger("TypeLowering")

interface TypeLowering : Lowering<ParameterValueDeclaration> {

    fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration

    fun lowerIdentificator(identificator: NameEntity): NameEntity {
        return when (identificator) {
            is IdentifierEntity -> identificator.copy(value = lowerIdentificator(identificator.value))
            is QualifierEntity -> identificator
            else -> raiseConcern("unknown NameEntity ${identificator}") { identificator }
        }
    }

    fun lowerIdentificator(identificator: String): String {
        return identificator;
    }

    fun lowerMethodNode(declaration: MethodNode): MethodNode {
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                parameters = declaration.parameters.map { parameter -> lowerParameterNode(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter)
                },
                type = lowerType(declaration.type)
        )
    }

    fun lowerPropertyNode(declaration: PropertyNode): PropertyNode {
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                type = lowerType(declaration.type),
                typeParameters = declaration.typeParameters.map { typeParameter -> lowerTypeParameter(typeParameter) }
        )
    }

    override fun lowerMemberNode(declaration: MemberNode): MemberNode {
        return when (declaration) {
            is MethodNode -> lowerMethodNode(declaration)
            is PropertyNode -> lowerPropertyNode(declaration)
            is ConstructorNode -> lowerConstructorNode(declaration)
            else -> {
                logger.debug("[${this}] skipping ${declaration}")
                declaration
            }
        }
    }

    override fun lowerFunctionNode(declaration: FunctionNode): FunctionNode {
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                parameters = declaration.parameters.map { parameter -> lowerParameterNode(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter)
                },
                type = lowerType(declaration.type)
        )
    }

    override fun lowerTypeParameter(declaration: TypeValueNode): TypeValueNode {
        return declaration.copy(
                value = lowerIdentificator(declaration.value),
                params = declaration.params.map { params -> lowerType(params) }
        )
    }

    override fun lowerUnionTypeNode(declaration: UnionTypeNode): UnionTypeNode {
        return declaration.copy(params = declaration.params.map { param -> lowerType(param) })
    }


    override fun lowerTypeNode(declaration: TypeValueNode): TypeValueNode {
        return declaration.copy(
            params = declaration.params.map { param -> lowerType(param) },
            meta = declaration.meta?.let { lowerType(it) }
        )
    }

    override fun lowerFunctionNode(declaration: FunctionTypeNode): FunctionTypeNode {
        return declaration.copy(
                parameters = declaration.parameters.map { param -> lowerParameterNode(param) },
                type = lowerType(declaration.type)
        )
    }

    override fun lowerParameterNode(declaration: ParameterNode): ParameterNode {
        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                type = lowerType(declaration.type)
        )
    }

    override fun lowerVariableNode(declaration: VariableNode): VariableNode {
        return declaration.copy(name = lowerIdentificator(declaration.name), type = lowerType(declaration.type))
    }

    fun lowerHeritageNode(heritageClause: HeritageNode): HeritageNode {
        val typeArguments = heritageClause.typeArguments.map { typeArgument ->
            // TODO: obviously very clumsy place
            lowerType(typeArgument)
        }
        return heritageClause.copy(typeArguments = typeArguments)
    }


    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {

        return declaration.copy(
                name = lowerIdentificator(declaration.name),
                members = declaration.members.map { member -> lowerMemberNode(member) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(heritageClause)
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter)
                }
        )
    }

    override fun lowerTypeAliasNode(declaration: TypeAliasNode, owner: DocumentRootNode): TypeAliasNode {
        return declaration.copy(typeReference = lowerType(declaration.typeReference))
    }

    fun lowerConstructorNode(declaration: ConstructorNode): ConstructorNode {
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterNode(parameter) },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter)
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
                name = lowerIdentificator(declaration.name),
                members = declaration.members.map { member -> lowerMemberNode(member) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageNode(heritageClause)
                },
                typeParameters = declaration.typeParameters.map { typeParameter ->
                    lowerTypeParameter(typeParameter)
                }
        )
    }
}