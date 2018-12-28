package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration

private fun Map<String, Any?>.parameterDeclarationToAst() : ParameterDeclaration {

    val initializer = get("initializer")?.let {
        val expression = (it as Map<String, Any?>).toAst()

        if (expression is Expression) {
            if (expression.kind.value == "@@DEFINED_EXTERNALLY") {
                expression
            } else throw Exception("unkown initializer")
        } else null
    }

    return ParameterDeclaration(
            get("name") as String,
            (get("type") as Map<String, Any>).toAst() as TypeDeclaration,
            initializer
    )
}

fun Map<String, Any?>.toAst(): Declaration {
    val reflectionType = AstReflectionType.valueOf(get("reflection") as String)
    val res: Declaration
    if (reflectionType == AstReflectionType.EXPRESSION_DECLARATION) {
        res = Expression(
                (get("kind") as Map<String, Any?>).toAst() as TypeDeclaration,
                get("meta") as String
        )
    } else if (reflectionType == AstReflectionType.TYPE_DECLARATION) {
        res = TypeDeclaration(if (get("value") is String) {
            get("value") as String
        } else {
            throw Exception("failed to create type declaration from ${this}")
        }, (get("params") as List<Map<String, Any?>>).map { it.toAst() as TypeDeclaration })
    } else if (reflectionType == AstReflectionType.FUNCTION_DECLARATION) {
        res = FunctionDeclaration(
                get("name") as String,
                (get("parameters") as List<Map<String, Any?>>).map { it.toAst() as ParameterDeclaration },
                (get("type") as Map<String, Any?>).toAst() as TypeDeclaration
        )
    } else if (reflectionType == AstReflectionType.PARAMETER_DECLARATION) {
        res = parameterDeclarationToAst()
    } else if (reflectionType == AstReflectionType.VARIABLE_DECLARATION) {
        res = VariableDeclaration(get("name") as String, (get("type") as Map<String, Any>).toAst() as TypeDeclaration)
    } else if (reflectionType == AstReflectionType.DOCUMENT_ROOT) {
        res = DocumentRoot((get("declarations") as List<Map<String, Any?>>).map {
            it.toAst()
        })
    } else {
        println(this.get("reflection"))
        throw Exception("failed to create declaration from mapper: ${this}")
    }

    return res
}