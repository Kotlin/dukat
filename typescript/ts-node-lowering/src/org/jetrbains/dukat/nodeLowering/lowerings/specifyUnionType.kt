package org.jetrbains.dukat.nodeLowering.lowerings

import cartesian
import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.MethodNodeMeta
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetrbains.dukat.nodeLowering.IdentityLowering

const val COMPLEXITY_THRESHOLD = 15

private fun specifyArguments(params: List<ParameterNode>): List<List<ParameterNode>> {

    var currentComplexity = 1

    return params.map { parameterDeclaration ->
        val type = parameterDeclaration.type
        when (type) {
            is UnionTypeNode -> {
                currentComplexity *= type.params.size
                if (currentComplexity <= COMPLEXITY_THRESHOLD) {
                    type.params.map { param ->
                        parameterDeclaration.copy(type = if (type.nullable) param.makeNullable() else param)
                    }
                } else {
                    listOf(parameterDeclaration)
                }
            }
            else -> {
                listOf(parameterDeclaration)
            }
        }
    }
}

private class SpecifyUnionTypeLowering : IdentityLowering {

    fun generateParams(params: List<ParameterNode>): Pair<List<List<ParameterNode>>, Boolean> {
        val specifyParams = specifyArguments(params)
        val hasUnrolledParams = specifyParams.any { it.size > 1 }

        return if (specifyParams.size == 1) {
            Pair(specifyParams.first().map { param -> listOf(param) }, hasUnrolledParams)
        } else {
            Pair(cartesian(*specifyParams.toTypedArray()), hasUnrolledParams)
        }
    }

    fun generateMethods(declaration: MethodNode): List<MethodNode> {
        val generatedParams = generateParams(declaration.parameters)
        return generatedParams.first.map { params ->
            declaration.copy(parameters = params, meta = MethodNodeMeta(generated = generatedParams.second))
        }
    }


    fun generateFunctionNodes(declaration: FunctionNode): List<FunctionNode> {
        return generateParams(declaration.parameters).first.map { params ->
            declaration.copy(parameters = params)
        }.distinctBy { node ->
            node.copy(
                    parameters = node.parameters.mapIndexed { index, param ->
                        val paramCopy = param.copy(
                                name = "p${index}",
                                type = param.type.duplicate()
                        )

                        paramCopy.type.meta = null
                        paramCopy
                    }
            )
        }
    }

    fun generateConstructors(declaration: ConstructorNode): List<ConstructorNode> {
        val hasDynamic = declaration.parameters.any { (it.type is UnionTypeNode) }

        return generateParams(declaration.parameters).first.map { params ->
            declaration.copy(parameters = params, generated = declaration.generated || hasDynamic)
        }.distinctBy { node ->
            node.copy(
                    parameters = node.parameters.mapIndexed { index, param ->
                        val paramCopy = param.copy(
                                name = "p${index}",
                                type = param.type.duplicate()
                        )

                        paramCopy.type.meta = null
                        paramCopy
                    }
            )
        }
    }

    override fun lowerClassNode(declaration: ClassNode): ClassNode {
        val members = declaration.members.map { member ->
            when (member) {
                is ConstructorNode -> generateConstructors(member)
                is MethodNode -> generateMethods(member)
                else -> listOf(member)
            }
        }.flatten()
        return declaration.copy(members = members)
    }

    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {
        //TODO: discuss whether we need this limitation at all
        val members = declaration.members.flatMap { member ->
            when (member) {
                is MethodNode -> generateMethods(member)
                else -> listOf(member)
            }
        }
        return declaration.copy(members = members)
    }

    fun lowerTopLevelDeclarationList(declaration: TopLevelEntity, owner: DocumentRootNode): List<TopLevelEntity> {
        return when (declaration) {
            is VariableNode -> listOf(lowerVariableNode(declaration))
            is FunctionNode -> generateFunctionNodes(declaration)
            is ClassNode -> listOf(lowerClassNode(declaration))
            is InterfaceNode -> listOf(lowerInterfaceNode(declaration))
            is DocumentRootNode -> listOf(lowerDocumentRoot(declaration))
            is TypeAliasNode -> listOf(lowerTypeAliasNode(declaration, owner))
            else -> listOf(declaration)
        }
    }

    override fun lowerTopLevelDeclarations(declarations: List<TopLevelEntity>, owner: DocumentRootNode): List<TopLevelEntity> {
        return declarations.flatMap { declaration ->
            lowerTopLevelDeclarationList(declaration, owner)
        }
    }

}


fun DocumentRootNode.specifyUnionType(): DocumentRootNode {
    return SpecifyUnionTypeLowering().lowerDocumentRoot(this)
}

fun SourceSetNode.specifyUnionType(): SourceSetNode = transform { it.specifyUnionType() }