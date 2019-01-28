package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.declaration.CallSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.declaration.ConstructorDeclaration
import org.jetbrains.dukat.ast.model.declaration.Declaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.ExpressionDeclaration
import org.jetbrains.dukat.ast.model.declaration.FunctionDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.MethodSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.ModifierDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.PropertyDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.VariableDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.IndexSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.StringTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.getEntity(key: String) = get(key) as Map<String, Any?>?

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.getEntitiesList(key: String) = get(key) as List<Map<String, Any?>>

private fun <T : Declaration> Map<String, Any?>.mapEntities(key: String, mapper: (Map<String, Any?>) -> T) =
        getEntitiesList(key).map(mapper)

private fun Map<String, Any?>.getInitializerExpression(): ExpressionDeclaration? {
    return getEntity("initializer")?.let {
        val expression = it.toAst<Declaration>()

        if (expression is ExpressionDeclaration) {
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
    val reflectionType = get("reflection") as String
    val res = when (reflectionType) {
        StringTypeDeclaration::class.simpleName -> StringTypeDeclaration(
                get(StringTypeDeclaration::tokens.name) as List<String>
        )
        IndexSignatureDeclaration::class.simpleName -> IndexSignatureDeclaration(
                mapEntities(IndexSignatureDeclaration::indexTypes.name) { it.toAst<ParameterDeclaration>() },
                getEntity(IndexSignatureDeclaration::returnType.name)!!.toAst()
        )
        CallSignatureDeclaration::class.simpleName -> CallSignatureDeclaration(
                mapEntities("parameters") { it.toAst<ParameterDeclaration>() },
                getEntity("type")!!.toAst(),
                mapEntities("typeParameters") { it.toAst<TypeParameterDeclaration>() }
        )
        ExpressionDeclaration::class.simpleName -> ExpressionDeclaration(
                (getEntity("kind"))!!.toAst(),
                get("meta") as String
        )
        ModifierDeclaration::class.simpleName -> ModifierDeclaration(get("token") as String)
        TypeDeclaration::class.simpleName -> TypeDeclaration(if (get("value") is String) {
            get("value") as String
        } else {
            throw Exception("failed to create type declaration from ${this}")
        }, mapEntities("params") { it.toAst<ParameterValueDeclaration>() })
        ConstructorDeclaration::class.simpleName -> ConstructorDeclaration(
                mapEntities("parameters") { it.toAst<ParameterDeclaration>() },
                getEntity("type")!!.toAst(),
                mapEntities("typeParameters") { it.toAst<TypeParameterDeclaration>() },
                mapEntities("modifiers") {it.toAst<ModifierDeclaration>()}
        )
        FunctionDeclaration::class.simpleName -> FunctionDeclaration(
                get("name") as String,
                mapEntities("parameters") { it.toAst<ParameterDeclaration>() },
                getEntity("type")!!.toAst(),
                mapEntities("typeParameters") { it.toAst<TypeParameterDeclaration>() },
                mapEntities("modifiers") {it.toAst<ModifierDeclaration>()}
        )
        MethodSignatureDeclaration::class.simpleName -> MethodSignatureDeclaration(
                get(MethodSignatureDeclaration::name.name) as String,
                mapEntities(MethodSignatureDeclaration::parameters.name) { it.toAst<ParameterDeclaration>() },
                getEntity(MethodSignatureDeclaration::type.name)!!.toAst(),
                mapEntities(MethodSignatureDeclaration::typeParameters.name) { it.toAst<TypeParameterDeclaration>() },
                get(MethodSignatureDeclaration::optional.name) as Boolean,
                mapEntities(MethodSignatureDeclaration::modifiers.name) {it.toAst<ModifierDeclaration>()}
        )
        FunctionTypeDeclaration::class.simpleName -> FunctionTypeDeclaration(
                mapEntities("parameters") { it.toAst<ParameterDeclaration>() },
                getEntity("type")!!.toAst()
        )
        ParameterDeclaration::class.simpleName -> parameterDeclarationToAst()
        VariableDeclaration::class.simpleName -> VariableDeclaration(get("name") as String, getEntity("type")!!.toAst())
        PropertyDeclaration::class.simpleName -> PropertyDeclaration(
                get("name") as String,
                getEntity("type")!!.toAst(),
                mapEntities("typeParameters") { it.toAst<TypeParameterDeclaration>() },
                get("optional") as Boolean,
                mapEntities("modifiers") {it.toAst<ModifierDeclaration>()}
        )
        DocumentRootDeclaration::class.simpleName -> DocumentRootDeclaration(get("packageName") as String, mapEntities("declarations") {
            it.toAst<Declaration>()
        })
        TypeParameterDeclaration::class.simpleName -> TypeParameterDeclaration(get("name") as String, mapEntities("constraints") { it.toAst<ParameterValueDeclaration>() })
        ClassDeclaration::class.simpleName -> ClassDeclaration(
                get("name") as String,
                mapEntities("members") { it.toAst<MemberDeclaration>() },
                mapEntities("typeParameters") { it.toAst<TypeParameterDeclaration>() },
                mapEntities("parentEntities") { it.toAst<ClassLikeDeclaration>() },
                null,
                mapEntities("staticMembers") { it.toAst<MemberDeclaration>() }
        )
        InterfaceDeclaration::class.simpleName -> InterfaceDeclaration(
                get("name") as String,
                mapEntities("members") { it.toAst<MemberDeclaration>() },
                mapEntities("typeParameters") { it.toAst<TypeParameterDeclaration>() },
                mapEntities("parentEntities") { it.toAst<InterfaceDeclaration>() }
        )
        ObjectLiteralDeclaration::class.simpleName -> ObjectLiteralDeclaration(mapEntities("members") { it.toAst<MemberDeclaration>() })
        else -> throw Exception("failed to create declaration from mapper: ${this}")
    }

    return res as T
}