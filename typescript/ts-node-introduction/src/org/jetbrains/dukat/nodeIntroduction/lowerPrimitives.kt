package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.isPrimitive
import org.jetbrains.dukat.ast.model.nodes.metadata.MuteMetadata
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.nodeIntroduction.ParameterValueLowering


private fun mapPrimitiveValue(value: String): String {
    return when (value) {
        "any" -> "Any"
        "boolean" -> "Boolean"
        "string" -> "String"
        "number" -> "Number"
        "Object" -> "Any"
        else -> value
    }
}

private fun NameEntity.mapPrimitive(): NameEntity {
    return when (this) {
        is IdentifierEntity -> {
            copy(value = mapPrimitiveValue(value))
        }
        else -> this
    }
}

private class PrimitiveClassLowering : ParameterValueLowering {
    override fun lowerTypeNode(declaration: TypeValueNode): TypeValueNode {
        if (declaration.value == IdentifierEntity("Function")) {
            return declaration.copy(params = listOf(TypeValueNode(IdentifierEntity("*"), emptyList())))
        }

        var value = declaration.value.mapPrimitive()
        var nullable = declaration.nullable
        var meta = declaration.meta

        if (declaration.isPrimitive("undefined") || declaration.isPrimitive("null")) {
            value = IdentifierEntity("Nothing")
            nullable = true
            meta = MuteMetadata()
        }

        return declaration.copy(
                value = value,
                params = declaration.params.map { lowerType(it) },
                typeReference = if (value != declaration.value) {
                    null
                } else {
                    declaration.typeReference
                },
                nullable = nullable,
                meta = meta
        )
    }
}

fun DocumentRootNode.lowerPrimitives(): DocumentRootNode {
    return PrimitiveClassLowering().lowerDocumentRoot(this)
}

fun SourceSetNode.lowerPrimitives() = transform { it.lowerPrimitives() }