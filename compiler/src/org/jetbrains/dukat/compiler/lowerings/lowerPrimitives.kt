package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeNodeValue
import org.jetbrains.dukat.compiler.lowerings.ParameterValueLowering
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration


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

private fun TypeNodeValue.mapPrimitive() : TypeNodeValue {
    return when(this) {
        is IdentifierNode -> {
            copy(value = mapPrimitiveValue(value))
        }
        else -> this
    }
}

private class PrimitiveClassLowering : ParameterValueLowering {
    override fun lowerTypeNode(declaration: TypeNode): TypeNode {
        if (declaration == TypeNode("Function", emptyList())) {
            return TypeNode("Function", listOf(TypeNode("*", emptyList())))
        }

        var value = declaration.value.mapPrimitive()
        var nullable = declaration.nullable

        if (declaration.isPrimitive("undefined") || declaration.isPrimitive("null")) {
            value = IdentifierNode("Nothing")
            nullable = true
        }

        return declaration.copy(
                value = value,
                params = declaration.params.map { lowerParameterValue(it) },
                nullable = nullable
        )
    }

    override fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration {
        return declaration.copy(name = mapPrimitiveValue(declaration.name))
    }
}

fun DocumentRootNode.lowerPrimitives(): DocumentRootNode {
    return PrimitiveClassLowering().lowerDocumentRoot(this)
}
