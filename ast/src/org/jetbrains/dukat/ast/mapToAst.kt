package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.Expression
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.MethodDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.PropertyDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter
import org.jetbrains.dukat.ast.model.VariableDeclaration

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.getEntity(key: String) = get(key) as Map<String, Any?>?

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.getEntitiesList(key: String) = get(key) as List<Map<String, Any?>>

private fun <T:Declaration> Map<String, Any?>.mapEntities(key: String, mapper: (Map<String, Any?>) -> T) =
        getEntitiesList(key).map(mapper)

private fun Map<String, Any?>.getInitializerExpression(): Expression? {
    return getEntity("initializer")?.let {
        val expression = it.toAst<Declaration>()

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
    val reflectionMarker = get("reflection") as String
    val reflectionType = AstReflectionType.valueOf(reflectionMarker)
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
        }, mapEntities("params") { it.toAst<ParameterValue>() })
    } else if (reflectionType == AstReflectionType.FUNCTION_DECLARATION) {
        res = FunctionDeclaration(
                get("name") as String,
                mapEntities("parameters") { it.toAst<ParameterDeclaration>() },
                getEntity("type")!!.toAst(),
                mapEntities("typeParameters") { it.toAst<TypeParameter>() }
        )
    } else if (reflectionType == AstReflectionType.METHOD_DECLARATION) {
            res = MethodDeclaration(
                    get("name") as String,
                    mapEntities("parameters") { it.toAst<ParameterDeclaration>() },
                    getEntity("type")!!.toAst(),
                    mapEntities("typeParameters") { it.toAst<TypeParameter>() },
                    get("override") as Boolean,
                    get("operator") as Boolean
            )
    } else if (reflectionType == AstReflectionType.FUNCTION_TYPE_DECLARATION) {
        res = FunctionTypeDeclaration(
                mapEntities("parameters") { it.toAst<ParameterDeclaration>() },
                getEntity("type")!!.toAst()
        )
    } else if (reflectionType == AstReflectionType.PARAM_TYPE_DECLARATION) {
        res = parameterDeclarationToAst()
    } else if (reflectionType == AstReflectionType.VARIABLE_DECLARATION) {
        res = VariableDeclaration(get("name") as String, getEntity("type")!!.toAst())
    } else if (reflectionType == AstReflectionType.PROPERTY_DECLARATION) {
        res = PropertyDeclaration(
                get("name") as String,
                getEntity("type")!!.toAst(),
                mapEntities("typeParameters") {it.toAst<TypeParameter>()},
                get("getter") as Boolean,
                get("setter") as Boolean,
                get("override") as Boolean
        )
    } else if (reflectionType == AstReflectionType.DOCUMENT_ROOT) {
        res = DocumentRoot(get("packageName") as String, mapEntities("declarations") {
            it.toAst<Declaration>()
        })
    } else if (reflectionType == AstReflectionType.TYPE_PARAM) {
        res = TypeParameter(get("name") as String, mapEntities("constraints") { it.toAst<ParameterValue>() })
    } else if (reflectionMarker == "CLASS_DECLARATION") {
        res = ClassDeclaration(
                get("name") as String,
                mapEntities("members") {it.toAst<MemberDeclaration>()},
                mapEntities("typeParameters") {it.toAst<TypeParameter>()},
                mapEntities("parentEntities") {it.toAst<ClassLikeDeclaration>()}
        )
    } else if (reflectionType == AstReflectionType.INTERFACE_DECLARATION) {
        res = InterfaceDeclaration(
                get("name") as String,
                mapEntities("members") {it.toAst<MemberDeclaration>()},
                mapEntities("typeParameters") {it.toAst<TypeParameter>()},
                mapEntities("parentEntities") { it.toAst<InterfaceDeclaration>() }
        )
    } else {
        throw Exception("failed to create declaration from mapper: ${this}")
    }

    return res as T
}