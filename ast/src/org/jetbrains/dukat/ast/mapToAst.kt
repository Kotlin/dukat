package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.getEntity(key: String) = get(key) as Map<String, Any?>?

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.getEntitiesList(key: String) = get(key) as List<Map<String, Any?>>

private fun Map<String, Any?>.getInitializerExpression(): Expression? {
    return getEntity("initializer")?.let {
        val expression = (it as Map<String, Any?>).toAst<Declaration>()

        if (expression is Expression) {
            if (expression.kind.value == "@@DEFINED_EXTERNALLY") {
                expression
            } else throw Exception("unkown initializer")
        } else null
    }
}

private fun Map<String, Any?>.parameterDeclarationToAst() =
        ParameterDeclaration(
                get("name") as String,
                (getEntity("type"))!!.toAst(),
                getInitializerExpression()
        )

@Suppress("UNCHECKED_CAST")
fun <T : AstNode> Map<String, Any?>.toAst(): T {
    val reflectionType = AstReflectionType.valueOf(get("reflection") as String)
    val res: Declaration
    if (reflectionType == AstReflectionType.EXPRESSION_DECLARATION) {
        res = Expression(
                (getEntity("kind"))!!.toAst(),
                get("meta") as String
        )
    } else if (reflectionType == AstReflectionType.TYPE_DECLARATION) {
        res = TypeDeclaration(if (get("value") is String) {
            get("value") as String
        } else {
            throw Exception("failed to create type declaration from ${this}")
        }, getEntitiesList("params").map { it.toAst<ParameterValue>() })
    } else if (reflectionType == AstReflectionType.FUNCTION_DECLARATION) {
        res = FunctionDeclaration(
                get("name") as String,
                getEntitiesList("parameters").map { it.toAst<ParameterDeclaration>() },
                getEntity("type")!!.toAst<ParameterValue>()
        )
    } else if (reflectionType == AstReflectionType.FUNCTION_TYPE_DECLARATION) {
        res = FunctionTypeDeclaration(
                getEntitiesList("parameters").map { it.toAst<ParameterDeclaration>() },
                getEntity("type")!!.toAst()
        )
    } else if (reflectionType == AstReflectionType.PARAM_TYPE_DECLARATION) {
        res = parameterDeclarationToAst()
    } else if (reflectionType == AstReflectionType.VARIABLE_DECLARATION) {
        res = VariableDeclaration(get("name") as String, getEntity("type")!!.toAst())
    } else if (reflectionType == AstReflectionType.DOCUMENT_ROOT) {
        res = DocumentRoot(getEntitiesList("declarations").map {
            it.toAst<Declaration>()
        })
    } else {
        println(this.get("reflection"))
        throw Exception("failed to create declaration from mapper: ${this}")
    }

    return res as T
}