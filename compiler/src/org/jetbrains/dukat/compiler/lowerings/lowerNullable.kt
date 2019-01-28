package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration


private class LowerNullable : ParameterValueLowering {

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeDeclaration -> {
                if (declaration.value == "@@Union") {
                    val params = declaration.params.filter { param ->
                        param != TypeDeclaration("undefined", emptyList()) &&
                                param != TypeDeclaration("null", emptyList())
                    }

                    if (params.size == 1) {
                        val nullableType = params[0]
                        if (nullableType is TypeDeclaration) {
                            val res = lowerTypeDeclaration(nullableType)
                            res.nullable = true
                            return res
                        } else if (nullableType is FunctionTypeDeclaration) {
                            val res = lowerFunctionTypeDeclaration(nullableType)
                            res.nullable = true
                            return res
                        } else {
                            throw Exception("can not lowerDocumentRoot nullables for unknown param type ${nullableType}")
                        }
                    } else lowerTypeDeclaration(declaration)
                } else {
                    return lowerTypeDeclaration(declaration)
                }
            }
            is FunctionTypeDeclaration -> {
                return lowerFunctionTypeDeclaration(declaration)
            }
            else -> throw Exception("can not lowerDocumentRoot nullables for unknown param type ${this}")
        }

    }
}

fun DocumentRootDeclaration.lowerNullable(): DocumentRootDeclaration {
    return LowerNullable().lowerDocumentRoot(this)
}
