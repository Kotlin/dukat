package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.MethodDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.PropertyDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter
import org.jetbrains.dukat.ast.model.VariableDeclaration


private fun ParameterDeclaration.toMap(): Map<String, Any?> {
    val map = mutableMapOf(
            "name" to name,
            "type" to type.astToMap()
    ).reflectAs(AstReflectionType.PARAM_TYPE_DECLARATION)

    initializer?.let {
        map.set("initializer", it.astToMap())
    }

    return map
}


private fun MutableMap<String, Any>.reflectAs(astReflectionType: AstReflectionType): MutableMap<String, Any> {
    this.put("reflection", astReflectionType.toString())
    return this
}

private fun List<AstNode>.astToMap() = this.map(AstNode::astToMap)


fun AstNode.astToMap(): Map<String, Any?> {
    return when (this) {
        is ClassDeclaration -> mutableMapOf(
                "name" to name,
                "members" to members.astToMap(),
                "typeParameters" to typeParameters.astToMap(),
                "parentEntities" to parentEntities.astToMap()
        ).reflectAs(AstReflectionType.CLASS_DECLARATION)
        is InterfaceDeclaration -> mutableMapOf(
                "name" to name,
                "members" to members.astToMap(),
                "typeParameters" to typeParameters.astToMap(),
                "parentEntities" to parentEntities.astToMap()
        ).reflectAs(AstReflectionType.INTERFACE_DECLARATION)
        is TypeParameter -> mutableMapOf(
                "name" to name,
                "constraints" to constraints.astToMap()
        ).reflectAs(AstReflectionType.TYPE_PARAM)
        is TypeDeclaration -> mutableMapOf(
                "value" to value,
                "params" to params.astToMap()
        ).reflectAs(AstReflectionType.TYPE_DECLARATION)
        is VariableDeclaration -> mutableMapOf(
                "name" to name,
                "type" to type.astToMap()
        ).reflectAs(AstReflectionType.VARIABLE_DECLARATION)
        is PropertyDeclaration -> mutableMapOf(
                "name" to name, "type" to type.astToMap(),
                "typeParameters" to typeParameters.astToMap(),
                "getter" to getter,
                "setter" to setter,
                "override" to override
        ).reflectAs(AstReflectionType.PROPERTY_DECLARATION)
        is ParameterDeclaration -> toMap()
        is FunctionDeclaration -> mutableMapOf(
                "name" to name,
                "type" to type.astToMap(),
                "parameters" to parameters.astToMap(),
                "typeParameters" to typeParameters.astToMap()
        ).reflectAs(AstReflectionType.FUNCTION_DECLARATION)
        is MethodDeclaration ->
            mutableMapOf(
                    "name" to name,
                    "type" to type.astToMap(),
                    "parameters" to parameters.astToMap(),
                    "typeParameters" to typeParameters.astToMap(),
                    "override" to override,
                    "operator" to operator
            ).reflectAs(AstReflectionType.METHOD_DECLARATION)
        is FunctionTypeDeclaration -> mutableMapOf(
                "type" to type.astToMap(),
                "parameters" to parameters.astToMap()).reflectAs(AstReflectionType.FUNCTION_TYPE_DECLARATION)
        is DocumentRoot -> mutableMapOf(
                "packageName" to packageName,
                "declarations" to declarations.astToMap()
        ).reflectAs(AstReflectionType.DOCUMENT_ROOT)
        is Expression -> (mutableMapOf(
                "kind" to kind.astToMap(),
                "meta" to meta) as MutableMap<String, Any>).reflectAs(AstReflectionType.EXPRESSION_DECLARATION)
        else -> throw Exception("can not map ${this}")
    }
}

