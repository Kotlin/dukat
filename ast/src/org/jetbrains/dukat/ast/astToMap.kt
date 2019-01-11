package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter
import org.jetbrains.dukat.ast.model.VariableDeclaration


private fun ParameterDeclaration.toMap(): Map<String, Any?> {
    val map = mutableMapOf(
            "reflection" to AstReflectionType.PARAM_TYPE_DECLARATION.toString(),
            "name" to name,
            "type" to type.astToMap()
    )

    initializer?.let {
        map.set("initializer", it.astToMap())
    }

    return map
}


fun AstNode.astToMap(): Map<String, Any?> {
    return when (this) {
        is TypeParameter -> mapOf(
                "reflection" to AstReflectionType.TYPE_PARAM.toString(),
                "name" to name,
                "constraints" to constraints.map(AstNode::astToMap)
        )
        is TypeDeclaration -> mapOf("reflection" to AstReflectionType.TYPE_DECLARATION.toString(), "value" to value, "params" to params.map(AstNode::astToMap))
        is VariableDeclaration -> mapOf("reflection" to AstReflectionType.VARIABLE_DECLARATION.toString(), "name" to name, "type" to type.astToMap())
        is ParameterDeclaration -> toMap()
        is FunctionDeclaration -> mapOf("reflection" to AstReflectionType.FUNCTION_DECLARATION.toString(),
                "name" to name,
                "type" to type.astToMap(),
                "parameters" to parameters.map(AstNode::astToMap),
                "typeParameters" to typeParameters.map(AstNode::astToMap)
        )
        is FunctionTypeDeclaration -> mapOf("reflection" to AstReflectionType.FUNCTION_TYPE_DECLARATION.toString(), "type" to type.astToMap(), "parameters" to parameters.map(AstNode::astToMap))
        is DocumentRoot -> mapOf("reflection" to AstReflectionType.DOCUMENT_ROOT.toString(), "declarations" to declarations.map(AstNode::astToMap))
        is Expression -> mapOf("reflection" to AstReflectionType.EXPRESSION_DECLARATION.toString(), "kind" to kind.astToMap(), "meta" to meta)
        else -> throw Exception("can not map ${this}")
    }
}

