package org.jetbrains.dukat.compiler.lowerings

import cartesian
import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private fun specifyArguments(params: List<ParameterDeclaration>, complexityThreshold: Int): List<List<ParameterDeclaration>> {

    var currentComplexity = 1

    return params.map { parameterDeclaration ->
        val type = parameterDeclaration.type
        when (type) {
            is UnionTypeNode -> {
                currentComplexity *= type.params.size
                if (currentComplexity <= 15) {
                    type.params.map {param ->
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

private fun ParameterValueDeclaration.description(): String {
    return when(this) {
        is TypeNode -> {
            val typeNodeValue = value
            when (typeNodeValue) {
                is IdentifierNode -> typeNodeValue.value
                else -> typeNodeValue.toString()
            }
        }
        else -> "${this::class.simpleName}"
    }
}

private class SpecifyUnionTypeLowering : IdentityLowering {

    fun generateParams(params: List<ParameterDeclaration>): List<List<ParameterDeclaration>> {
        val specifyParams = specifyArguments(params, 16)

        return if (specifyParams.size == 1) {
            specifyParams.first().map { param -> listOf(param) }
        } else {
            cartesian(*specifyParams.toTypedArray())
        }
    }

    fun generateFunctionNodes(declaration: FunctionNode): List<FunctionNode> {
        return generateParams(declaration.parameters).map { params ->
            declaration.copy(parameters = params)
        }
    }

    fun generateConstructors(declaration: ConstructorNode): List<ConstructorNode> {
        val hasDynamic = declaration.parameters.any { (it.type is UnionTypeNode) }

        return generateParams(declaration.parameters).map { params ->
            declaration.copy(parameters = params, generated = declaration.generated || hasDynamic)
        }
    }

    fun generateMethods(declaration: MethodNode): List<MethodNode> {
        return generateParams(declaration.parameters).map { params ->
            declaration.copy(parameters = params)
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

    fun lowerTopLevelDeclarationList(declaration: TopLevelDeclaration): List<TopLevelDeclaration> {
        return when (declaration) {
            is VariableNode -> listOf(lowerVariableNode(declaration))
            is FunctionNode -> generateFunctionNodes(declaration)
            is ClassNode -> listOf(lowerClassNode(declaration))
            is InterfaceNode -> listOf(lowerInterfaceNode(declaration))
            is DocumentRootNode -> listOf(lowerDocumentRoot(declaration))
            is TypeAliasDeclaration -> listOf(lowerTypeAliasDeclaration(declaration))
            else -> listOf(declaration)
        }
    }

    override fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>): List<TopLevelDeclaration> {
        return declarations.flatMap { declaration ->
            lowerTopLevelDeclarationList(declaration)
        }
    }

}


fun DocumentRootNode.specifyUnionType(): DocumentRootNode {
    return SpecifyUnionTypeLowering().lowerDocumentRoot(this)
}

fun SourceSetNode.specifyUnionType(): SourceSetNode = transform { it.specifyUnionType() }