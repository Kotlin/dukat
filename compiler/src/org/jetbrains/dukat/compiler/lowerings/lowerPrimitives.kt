package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.compiler.lowerings.ParameterValueLowering
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


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

private class PrimitiveClassLowering : ParameterValueLowering {
    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        var value = mapPrimitiveValue(declaration.value)
        var nullable = declaration.nullable
        if ((value == "undefined") || (value == "null")) {
            value = "Nothing"
            nullable = true
        }

        return  declaration.copy(
                value = value,
                params = declaration.params.map { lowerParameterValue(it) },
                nullable = nullable
        )
    }

    override fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration {
        return declaration.copy(name = mapPrimitiveValue(declaration.name))
    }
}

fun DocumentRootDeclaration.lowerPrimitives(): DocumentRootDeclaration {
    return PrimitiveClassLowering().lowerDocumentRoot(this)
}
