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
import org.jetbrains.dukat.ast.model.extended.ObjectLiteral


private fun ParameterDeclaration.toMap(): Map<String, Any?> {
    val map = mapOf(
            "name" to name,
            "type" to type.astToMap()
    ).reflectAs(this).toMutableMap()

    initializer?.let {
        map.set("initializer", it.astToMap())
    }

    return map
}

private fun Map<String, *>.reflectAs(reflection: Any): Map<String, *> {
    val map = toMutableMap()
    map.put("reflection", reflection::class.simpleName)
    return map
}

private fun List<AstNode>.astToMap() = this.map(AstNode::astToMap)


fun AstNode.astToMap(): Map<String, Any?> {
    return when (this) {
        is ClassDeclaration -> mapOf(
                "name" to name,
                "members" to members.astToMap(),
                "typeParameters" to typeParameters.astToMap(),
                "parentEntities" to parentEntities.astToMap()
        ).reflectAs(this)
        is InterfaceDeclaration -> mapOf(
                "name" to name,
                "members" to members.astToMap(),
                "typeParameters" to typeParameters.astToMap(),
                "parentEntities" to parentEntities.astToMap()
        ).reflectAs(this)
        is TypeParameter -> mapOf(
                "name" to name,
                "constraints" to constraints.astToMap()
        ).reflectAs(this)
        is TypeDeclaration -> mapOf(
                "value" to value,
                "params" to params.astToMap()
        ).reflectAs(this)
        is VariableDeclaration -> mapOf(
                "name" to name,
                "type" to type.astToMap()
        ).reflectAs(this)
        is ObjectLiteral -> mapOf(
                "members" to members.astToMap()
        ).reflectAs(this)
        is PropertyDeclaration -> mapOf(
                "name" to name, "type" to type.astToMap(),
                "typeParameters" to typeParameters.astToMap(),
                "getter" to getter,
                "setter" to setter,
                "override" to override
        ).reflectAs(this)
        is ParameterDeclaration -> toMap()
        is FunctionDeclaration -> mapOf(
                "name" to name,
                "type" to type.astToMap(),
                "parameters" to parameters.astToMap(),
                "typeParameters" to typeParameters.astToMap()
        ).reflectAs(this)
        is MethodDeclaration ->
            mapOf(
                    "name" to name,
                    "type" to type.astToMap(),
                    "parameters" to parameters.astToMap(),
                    "typeParameters" to typeParameters.astToMap(),
                    "override" to override,
                    "operator" to operator
            ).reflectAs(this)
        is FunctionTypeDeclaration -> mapOf(
                "type" to type.astToMap(),
                "parameters" to parameters.astToMap()).reflectAs(this)
        is DocumentRoot -> mapOf(
                "packageName" to packageName,
                "declarations" to declarations.astToMap()
        ).reflectAs(this)
        is Expression -> (mapOf(
                "kind" to kind.astToMap(),
                "meta" to meta)).reflectAs(this)
        else -> throw Exception("can not map ${this}")
    }
}

