package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.UnionTypeDeclaration
import org.jetbrains.dukat.ast.model.makeNullable


private class LowerNullable : ParameterValueLowering {

    override fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration {
        return declaration.copy(type = if (declaration.optional) declaration.type.makeNullable() else declaration.type)
    }

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is UnionTypeDeclaration -> {
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
                        throw Exception("can not lower nullables for unknown param type ${nullableType}")
                    }
                } else lowerUnionTypeDeclation(declaration)
            }
            is FunctionTypeDeclaration -> {
                return lowerFunctionTypeDeclaration(declaration)
            }
            is TypeDeclaration -> {
                return lowerTypeDeclaration(declaration)
            }
            else -> declaration
        }

    }
}

fun DocumentRootDeclaration.lowerNullable(): DocumentRootDeclaration {
    return LowerNullable().lowerDocumentRoot(this)
}
