package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter
import org.jetbrains.dukat.compiler.lowerings.ParameterValueLowering


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

private class PrimitiveClassLowering : ParameterValueLowering() {
    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        return  declaration.copy(
                value =  mapPrimitiveValue(declaration.value),
                params = declaration.params.map { lowerParameterValue(it) }
        )
    }

    override fun lowerTypeParameter(declaration: TypeParameter): TypeParameter {
        return declaration.copy(name = mapPrimitiveValue(declaration.name))
    }
}

fun DocumentRoot.lowerPrimitives(): DocumentRoot {
    return PrimitiveClassLowering().lowerDocumentRoot(this)
}
