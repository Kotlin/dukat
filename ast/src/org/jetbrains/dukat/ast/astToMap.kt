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
import kotlin.reflect.KClass


private fun ParameterDeclaration.toMap(): Map<String, Any?> {
    val map = mapOf(
            "name" to name,
            "type" to type.astToMap()
    ).reflectAs(ParameterDeclaration::class).toMutableMap()

    initializer?.let {
        map.set("initializer", it.astToMap())
    }

    return map
}


private fun Map<String, *>.reflectAs(reflection: KClass<*>): Map<String, *> {
    val map = toMutableMap()
    map.put("reflection", reflection.simpleName)
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
        ).reflectAs(ClassDeclaration::class)
        is InterfaceDeclaration -> mapOf(
                "name" to name,
                "members" to members.astToMap(),
                "typeParameters" to typeParameters.astToMap(),
                "parentEntities" to parentEntities.astToMap()
        ).reflectAs(InterfaceDeclaration::class)
        is TypeParameter -> mapOf(
                "name" to name,
                "constraints" to constraints.astToMap()
        ).reflectAs(TypeParameter::class)
        is TypeDeclaration -> mapOf(
                "value" to value,
                "params" to params.astToMap()
        ).reflectAs(TypeDeclaration::class)
        is VariableDeclaration -> mapOf(
                "name" to name,
                "type" to type.astToMap()
        ).reflectAs(VariableDeclaration::class)
        is PropertyDeclaration -> mapOf(
                "name" to name, "type" to type.astToMap(),
                "typeParameters" to typeParameters.astToMap(),
                "getter" to getter,
                "setter" to setter,
                "override" to override
        ).reflectAs(PropertyDeclaration::class)
        is ParameterDeclaration -> toMap()
        is FunctionDeclaration -> mapOf(
                "name" to name,
                "type" to type.astToMap(),
                "parameters" to parameters.astToMap(),
                "typeParameters" to typeParameters.astToMap()
        ).reflectAs(FunctionDeclaration::class)
        is MethodDeclaration ->
            mapOf(
                    "name" to name,
                    "type" to type.astToMap(),
                    "parameters" to parameters.astToMap(),
                    "typeParameters" to typeParameters.astToMap(),
                    "override" to override,
                    "operator" to operator
            ).reflectAs(MethodDeclaration::class)
        is FunctionTypeDeclaration -> mapOf(
                "type" to type.astToMap(),
                "parameters" to parameters.astToMap()).reflectAs(FunctionTypeDeclaration::class)
        is DocumentRoot -> mapOf(
                "packageName" to packageName,
                "declarations" to declarations.astToMap()
        ).reflectAs(DocumentRoot::class)
        is Expression -> (mapOf(
                "kind" to kind.astToMap(),
                "meta" to meta)).reflectAs(Expression::class)
        else -> throw Exception("can not map ${this}")
    }
}

