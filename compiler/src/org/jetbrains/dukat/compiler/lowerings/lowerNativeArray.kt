package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class NativeArrayLowering : ParameterValueLowering {
    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        if (declaration.value == "@@ArraySugar") {
            return declaration.copy(value = "Array", params = declaration.params.map { param -> lowerParameterValue(param) })
        } else {
            return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
        }
    }
}

fun DocumentRootDeclaration.lowerNativeArray(): DocumentRootDeclaration {
    return NativeArrayLowering().lowerDocumentRoot(this)
}
