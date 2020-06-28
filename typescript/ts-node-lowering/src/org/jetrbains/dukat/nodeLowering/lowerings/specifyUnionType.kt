package org.jetrbains.dukat.nodeLowering.lowerings

import cartesian
import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.ParameterOwnerNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetrbains.dukat.nodeLowering.TopLevelNodeLowering

const val COMPLEXITY_THRESHOLD = 15

private fun specifyArguments(params: List<ParameterNode>): List<List<ParameterNode>> {

    var currentComplexity = 1

    return params.map { parameterDeclaration ->
        when (val type = parameterDeclaration.type) {
            is UnionTypeNode -> {
                currentComplexity *= type.params.size
                if (currentComplexity <= COMPLEXITY_THRESHOLD) {
                    type.params.map { param ->
                        parameterDeclaration.copy(type = param)
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

private class SpecifyUnionTypeLowering(private val generatedMethodsMap: MutableMap<String, MutableList<MethodNode>>) : TopLevelNodeLowering {

    fun generateParams(params: List<ParameterNode>): Pair<List<List<ParameterNode>>, Boolean> {
        val specifyParams = specifyArguments(params)
        val hasUnrolledParams = specifyParams.any { it.size > 1 }

        return if (specifyParams.size == 1) {
            Pair(specifyParams.first().map { param -> listOf(param) }, hasUnrolledParams)
        } else {
            Pair(cartesian(*specifyParams.toTypedArray()), hasUnrolledParams)
        }
    }

    fun generateMethods(declaration: MethodNode, uid: String?): List<MethodNode> {
        val generatedParams = generateParams(declaration.parameters)
        return generatedParams.first.map { params ->
            val declarationResolved = declaration.copy(parameters = params)
            if (generatedParams.second && (uid != null)) {
                generatedMethodsMap.getOrPut(uid) { mutableListOf() }.add(declarationResolved)
            }

            declarationResolved
        }
    }

    private fun ParameterOwnerNode.asKey() = parameters.map { param ->
        param.type.duplicate<TypeNode>().let {
            it.meta = null
            it
        }
    }

    fun generateFunctionNodes(declaration: FunctionNode): List<FunctionNode> {
        return generateParams(declaration.parameters).first.map { params ->
            declaration.copy(parameters = params)
        }.distinctBy { node -> node.asKey() }
    }

    fun generateConstructors(declaration: ConstructorNode): List<ConstructorNode> {
        return generateParams(declaration.parameters).first.map { params ->
            declaration.copy(parameters = params)
        }.distinctBy { node -> node.asKey() }
    }

    override fun lowerClassNode(declaration: ClassNode): ClassNode {
        val members = declaration.members.flatMap { member ->
            when (member) {
                is ConstructorNode -> generateConstructors(member)
                is MethodNode -> generateMethods(member, null)
                else -> listOf(member)
            }
        }
        return declaration.copy(members = members)
    }

    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {
        //TODO: discuss whether we need this limitation at all
        val members = declaration.members.flatMap { member ->
            when (member) {
                is MethodNode -> generateMethods(member, declaration.uid)
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

private class IntroduceGeneratedMembersToDescendantClasses(private val generatedMembersMap: Map<String, List<MethodNode>>) : TopLevelNodeLowering {
    override fun lowerClassNode(declaration: ClassNode): ClassNode {
        val generatedMembers = declaration.parentEntities.mapNotNull { generatedMembersMap[it.reference?.uid] }.flatten()
        return super.lowerClassNode(declaration.copy(members = declaration.members + generatedMembers))
    }
}

class SpecifyUnionType() : NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        val generatedMethodsMap = mutableMapOf<String, MutableList<MethodNode>>()

        val unrolledSourceSet = source.copy(sources = source.sources.map { sourceFile ->
            sourceFile.copy(root = SpecifyUnionTypeLowering(generatedMethodsMap).lowerModuleNode(sourceFile.root))
        })

        return unrolledSourceSet.copy(sources = unrolledSourceSet.sources.map { sourceFile ->
            sourceFile.copy(root = IntroduceGeneratedMembersToDescendantClasses(generatedMethodsMap).lowerModuleNode(sourceFile.root))
        })
    }
}