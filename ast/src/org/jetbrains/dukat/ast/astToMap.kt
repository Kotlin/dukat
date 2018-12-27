package org.jetbrains.dukat.ast

fun AstNode.astToMap(): Map<String, Any?> {
    return when (this) {
        is TypeDeclaration -> mapOf("reflection" to AstReflectionType.TYPE_DECLARATION.toString(), "value" to value, "params" to params.map(AstNode::astToMap))
        is VariableDeclaration -> mapOf("reflection" to AstReflectionType.VARIABLE_DECLARATION.toString(), "name" to name, "type" to type.astToMap())
        is ParameterDeclaration -> mapOf("reflection" to AstReflectionType.PARAMETER_DECLARATION.toString(), "name" to name, "type" to type.astToMap())
        is FunctionDeclaration -> mapOf("reflection" to AstReflectionType.FUNCTION_DECLARATION.toString(), "name" to name, "type" to type.astToMap(), "parameters" to parameters.map(AstNode::astToMap))
        is DocumentRoot -> mapOf("reflection" to AstReflectionType.DOCUMENT_ROOT.toString(), "declarations" to declarations.map(AstNode::astToMap))
        else -> throw Exception("can not map ${this}")
    }
}

