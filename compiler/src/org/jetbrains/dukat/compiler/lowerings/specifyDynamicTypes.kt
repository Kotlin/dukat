package org.jetbrains.dukat.compiler.lowerings

import cartesian
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration

private fun specifyArguments(params: List<ParameterDeclaration>): List<List<ParameterDeclaration>> {
    return params.map { parameterDeclaration ->
        val type = parameterDeclaration.type
        if (type is UnionTypeNode) {
            type.params.map { parameterDeclaration.copy(type = it) }
        } else listOf(parameterDeclaration)
    }
}

private class SpecifyDynamicTypesLowering : IdentityLowering {

    fun generateParams(params: List<ParameterDeclaration>): List<List<ParameterDeclaration>> {
        val specifyParams = specifyArguments(params)
        return cartesian(*specifyParams.toTypedArray())
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


fun DocumentRootNode.specifyDynamicTypes(): DocumentRootNode {
    return SpecifyDynamicTypesLowering().lowerDocumentRoot(this)
}