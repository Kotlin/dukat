package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.LiteralUnionNode
import org.jetbrains.dukat.ast.model.nodes.UnionLiteralKind
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.NumericLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private fun UnionTypeDeclaration.canBeTranslatedAsStringLiteral(): Boolean {
    return params.all { it is StringLiteralDeclaration }
}

private fun UnionTypeDeclaration.canBeTranslatedAsNumericLiteral(): Boolean {
    return params.all { it is NumericLiteralDeclaration }
}

enum class PARAMETER_CONTEXT {
    IRRELEVANT,
    FUNCTION_TYPE
}

private fun TypeDeclaration.isPrimitive(primitive: String): Boolean {
    return when (this.value) {
        is IdentifierEntity -> (value as IdentifierEntity).value == primitive
        else -> false
    }
}

private fun ParameterValueDeclaration.extractVarargType(): ParameterValueDeclaration {
    if (this is TypeDeclaration) {
        when {
            isPrimitive("Array") -> return params[0]
            isPrimitive("Any") -> return this
        }
    }

    return this
}

fun ParameterDeclaration.convertToNode(context: PARAMETER_CONTEXT = PARAMETER_CONTEXT.IRRELEVANT): ParameterDeclaration {
    var varargResolved = vararg
    val parameterValueDeclaration = if (vararg) {
        if (context == PARAMETER_CONTEXT.IRRELEVANT) {
            type.extractVarargType()
        } else {
            varargResolved = false
            type.extractVarargType()
        }
    } else {
        type
    }
    return ParameterDeclaration(
            name = name,
            type = parameterValueDeclaration.convertToNode(),
            initializer = initializer,
            vararg = varargResolved,
            optional = optional,
            explicitlyDeclaredType = explicitlyDeclaredType
    )
}

fun ParameterValueDeclaration.convertToNodeNullable(metaData: MetaData? = null): ParameterValueDeclaration? {
    return when (this) {
        is TypeParamReferenceDeclaration -> copy(meta = metaData ?: meta)
        is TypeDeclaration -> copy(
                params = params.map { param -> param.convertToNode() },
                meta = metaData ?: meta
        )
        //TODO: investigate where we still have FunctionTypeDeclarations up to this point
        is FunctionTypeDeclaration -> copy(
                parameters = parameters.map { parameterDeclaration ->
                    parameterDeclaration.convertToNode(PARAMETER_CONTEXT.FUNCTION_TYPE)
                },
                type = type.convertToNode(),
                meta = metaData ?: meta
        )
        is GeneratedInterfaceReferenceDeclaration -> copy(
                typeParameters = typeParameters.map { it.convertToNode() },
                meta = metaData ?: meta
        )
        is IntersectionTypeDeclaration -> {
            params[0].convertToNodeNullable(IntersectionMetadata(params.map {
                it.convertToNodeNullable() ?: it
            }))
        }
        is StringLiteralDeclaration -> {
            LiteralUnionNode(
                    params = listOf("\"$token\""),
                    kind = UnionLiteralKind.STRING,
                    nullable = nullable
            )
        }
        is NumericLiteralDeclaration -> {
            LiteralUnionNode(
                    params = listOf(token),
                    kind = UnionLiteralKind.NUMBER,
                    nullable = nullable
            )
        }
        is UnionTypeDeclaration ->
            when {
                canBeTranslatedAsStringLiteral() -> {
                    LiteralUnionNode(
                            params = params.mapNotNull { (it as? StringLiteralDeclaration)?.token }.map { "\"$it\"" },
                            kind = UnionLiteralKind.STRING,
                            nullable = nullable
                    )
                }
                canBeTranslatedAsNumericLiteral() -> {
                    LiteralUnionNode(
                            params = params.mapNotNull { (it as? NumericLiteralDeclaration)?.token },
                            kind = UnionLiteralKind.NUMBER,
                            nullable = nullable
                    )
                }
                else -> {
                    copy(
                            params = params.map { param -> param.convertToNode() },
                            meta = metaData ?: meta
                    )
                }
            }
        is TupleDeclaration -> copy(
                params = params.map { param -> param.convertToNode() },
                meta = metaData ?: meta
        )
        is ObjectLiteralDeclaration -> copy(members = members.flatMap { member -> convertMemberDeclaration(member, true) })
        is LiteralUnionNode -> this
        else -> null
    }
}

private val TYPE_ANY = TypeDeclaration(IdentifierEntity("Any"), emptyList(), null, false)

fun ParameterValueDeclaration.convertToNode(meta: MetaData? = null): ParameterValueDeclaration {
    return convertToNodeNullable(meta) ?: TYPE_ANY
}
