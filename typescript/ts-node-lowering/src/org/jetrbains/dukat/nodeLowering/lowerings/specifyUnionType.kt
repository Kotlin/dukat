package org.jetrbains.dukat.nodeLowering.lowerings

import cartesian
import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorParameterNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.MethodNodeMeta
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.changeName
import org.jetbrains.dukat.ast.model.nodes.changeType
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetrbains.dukat.nodeLowering.TopLevelNodeLowering

const val COMPLEXITY_THRESHOLD = 15

private fun specifyArguments(params: List<ConstructorParameterNode>): List<List<ConstructorParameterNode>> {

    var currentComplexity = 1

    return params.map { parameterDeclaration ->
        when (val type = parameterDeclaration.type) {
            is UnionTypeNode -> {
                currentComplexity *= type.params.size
                if (currentComplexity <= COMPLEXITY_THRESHOLD) {
                    type.params.map { param ->
                        parameterDeclaration.changeType(if (type.nullable) param.makeNullable() else param)
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

private class SpecifyUnionTypeLowering : TopLevelNodeLowering {

    fun generateParams(params: List<ConstructorParameterNode>): Pair<List<List<ConstructorParameterNode>>, Boolean> {
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
            declaration.copy(parameters = params.filterIsInstance<ParameterNode>(), meta = MethodNodeMeta(generated = generatedParams.second))
        }
    }


    fun generateFunctionNodes(declaration: FunctionNode): List<FunctionNode> {
        return generateParams(declaration.parameters).first.map { params ->
            declaration.copy(parameters = params.filterIsInstance<ParameterNode>())
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
                        val paramCopy = param.changeType(
                                param.type.duplicate()
                        ).changeName(
                            "p${index}"
                        )

                        paramCopy.type.meta = null
                        paramCopy
                    }
            )
        }
    }

    override fun lowerClassNode(declaration: ClassNode): ClassNode {
        val members = declaration.members.flatMap { member ->
            when (member) {
                is ConstructorNode -> generateConstructors(member)
                is MethodNode -> generateMethods(member)
                else -> listOf(member)
            }
        }
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

    fun lowerTopLevelDeclarationList(declaration: TopLevelNode, owner: ModuleNode): List<TopLevelNode> {
        return when (declaration) {
            is VariableNode -> listOf(lowerVariableNode(declaration))
            is FunctionNode -> generateFunctionNodes(declaration)
            is ClassNode -> listOf(lowerClassNode(declaration))
            is InterfaceNode -> listOf(lowerInterfaceNode(declaration))
            is ModuleNode -> listOf(lowerModuleNode(declaration))
            is TypeAliasNode -> listOf(lowerTypeAliasNode(declaration, owner))
            else -> listOf(declaration)
        }
    }

    override fun lowerTopLevelDeclarations(declarations: List<TopLevelNode>, owner: ModuleNode): List<TopLevelNode> {
        return declarations.flatMap { declaration ->
            lowerTopLevelDeclarationList(declaration, owner)
        }
    }

}


private fun ModuleNode.specifyUnionType(): ModuleNode {
    return SpecifyUnionTypeLowering().lowerModuleNode(this)
}

private fun SourceSetNode.specifyUnionType(): SourceSetNode = transform { it.specifyUnionType() }

class SpecifyUnionType(): NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        return source.specifyUnionType()
    }
}