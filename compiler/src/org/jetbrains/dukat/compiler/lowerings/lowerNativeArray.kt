package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.TypeDeclaration

private class NativeArrayLowering : ParameterValueLowering() {
    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        if (declaration.value == "@@ArraySugar") {
            return declaration.copy(value = "Array", params = declaration.params.map { param -> lowerParameterValue(param) })
        } else {
            return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
        }
    }
}

fun DocumentRoot.lowerNativeArray(): DocumentRoot {
    return NativeArrayLowering().lowerDocumentRoot(this)
}
