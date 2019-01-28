package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

private fun ParameterValueDeclaration.extractVarargType(): ParameterValueDeclaration? {
    if (this is TypeDeclaration) {
        if (value == "Array") {
            return params[0]
        } else if (value == "Any") {
            return this
        }
    }

    return null
}

private class LoweringVarags : ParameterValueLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        if (declaration is TypeDeclaration) {
            if (declaration.value == "@@Vararg") {
                declaration.params[0].extractVarargType()?.let {
                    if (it is TypeDeclaration) {
                        return it.copy(vararg = true, params = it.params.map { param -> lowerParameterValue(param) })
                    } else if (it is FunctionTypeDeclaration) {
                        return it.copy(vararg = true)
                    } else {
                        throw Exception("lowerVarargs called on unhandled type ${it}")
                    }
                }
            }

            return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
        } else if (declaration is FunctionTypeDeclaration) {
            return lowerFunctionTypeDeclaration(declaration)
        } else {
            throw Exception("lowerNativeArray called on unhandled type ${this}")
        }
    }
}

fun DocumentRootDeclaration.lowerVarargs(): DocumentRootDeclaration {
    return LoweringVarags().lowerDocumentRoot(this)
}