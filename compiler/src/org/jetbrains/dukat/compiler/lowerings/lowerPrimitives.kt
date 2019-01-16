package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.compiler.lowerings.ParameterValueLowering


private class PrimitiveClassLowering : ParameterValueLowering() {
    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        return when (declaration.value) {
            "any" -> declaration.copy(value = "Any", params = declaration.params.map { lowerParameterValue(it) })
            "boolean" -> declaration.copy(value = "Boolean", params = declaration.params.map { lowerParameterValue(it) })
            "string" -> declaration.copy(value = "String", params = declaration.params.map { lowerParameterValue(it) })
            "number" -> declaration.copy(value = "Number", params = declaration.params.map { lowerParameterValue(it) })
            else -> declaration.copy(params = declaration.params.map { lowerParameterValue(it) })
        }
    }
}

fun DocumentRoot.lowerPrimitives(): DocumentRoot {
    return PrimitiveClassLowering().lower(this)
}
