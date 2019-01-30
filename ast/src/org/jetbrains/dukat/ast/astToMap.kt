package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.AstNode
import org.jetbrains.dukat.ast.model.declaration.CallSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.ConstructorDeclaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.ExpressionDeclaration
import org.jetbrains.dukat.ast.model.declaration.FunctionDeclaration
import org.jetbrains.dukat.ast.model.declaration.HeritageClauseDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.MethodSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.ModifierDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.PropertyDeclaration
import org.jetbrains.dukat.ast.model.declaration.TokenDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeAliasDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.VariableDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.IndexSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.StringTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.UnionTypeDeclaration
import kotlin.reflect.KProperty0


private fun ParameterDeclaration.toMap(): Map<String, Any?> {
    val map = listOf(
            ::name,
            ::type,
            ::vararg,
            ::optional
    ).convert(this).toMutableMap()


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

private fun Any.toMap(): Any {
    return when (this) {
        is Boolean -> this
        is String -> this
        is AstNode -> astToMap()
        is List<*> -> this.map { it?.toMap() }
        else -> throw Exception("can not map ${this}")
    }
}

private fun List<KProperty0<*>>.convert(reflection: Any) =
    map { property -> Pair(property.name, property.getter())}
            .associateBy ({ it.first }, {it.second?.toMap()}).reflectAs(reflection)


fun AstNode.astToMap(): Map<String, Any?> {
    return when (this) {
        is CallSignatureDeclaration -> listOf(::type, ::parameters, ::typeParameters).convert(this)
        is ClassDeclaration -> listOf(::name, ::members, ::typeParameters, ::parentEntities, ::modifiers).convert(this)
        is ConstructorDeclaration -> listOf(::type, ::parameters, ::typeParameters, ::modifiers).convert(this)
        is DocumentRootDeclaration -> listOf(::packageName, ::declarations).convert(this)
        is ExpressionDeclaration -> listOf(::kind, ::meta).convert(this)
        is FunctionDeclaration -> listOf(::name, ::type, ::parameters, ::typeParameters, ::modifiers).convert(this)
        is FunctionTypeDeclaration -> listOf(::type, ::parameters).convert(this)
        is HeritageClauseDeclaration -> listOf(::name, ::typeArguments, ::extending).convert(this)
        is IndexSignatureDeclaration -> listOf(::returnType, ::indexTypes).convert(this)
        is InterfaceDeclaration -> listOf(::name, ::members, ::typeParameters, ::parentEntities).convert(this)
        is MethodSignatureDeclaration -> listOf(::name, ::type, ::parameters, ::typeParameters, ::optional, ::modifiers).convert(this)
        is ModifierDeclaration -> listOf(::token).convert(this)
        is ObjectLiteralDeclaration -> listOf(::members).convert(this)
        is ParameterDeclaration -> toMap()
        is PropertyDeclaration -> listOf(::name, ::type, ::typeParameters, ::optional, ::modifiers).convert(this)
        is StringTypeDeclaration -> listOf(::tokens).convert(this)
        is TokenDeclaration -> listOf(::value).convert(this)
        is TypeAliasDeclaration -> listOf(::aliasName, ::typeParameters, ::typeReference).convert(this)
        is TypeDeclaration -> listOf(::value, ::params).convert(this)
        is TypeParameterDeclaration -> listOf(::name, ::constraints).convert(this)
        is UnionTypeDeclaration -> listOf(::params).convert(this)
        is VariableDeclaration -> listOf(::name, ::type).convert(this)
        else -> throw Exception("can not map ${this}")
    }
}

