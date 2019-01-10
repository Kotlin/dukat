package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.duplicate

private fun ParameterValue.copyNullableType(): ParameterValue? {
    if (this is TypeDeclaration) {
        if (value != "@@Union") {
            return null
        }

        val params = params.filter { param ->
            param != TypeDeclaration("undefined", emptyList()) &&
                  param != TypeDeclaration("null", emptyList())
        }

        if (params.size == 1) {
            val nullableType = params[0]
            return nullableType.duplicate()
        }
    }

    return null
}

private fun ParameterValue.lowerNullableType(): ParameterValue {

    copyNullableType()?.let { parameterValue ->
        if (parameterValue is TypeDeclaration) {
            return TypeDeclaration(
                    parameterValue.value + "?",
                    parameterValue.params.map { it.lowerNullableType()}.toTypedArray()
            )
        } else if (parameterValue is FunctionTypeDeclaration) {
            return parameterValue.copy(nullable = true)        }
    }

    return duplicate()
}


private fun ParameterDeclaration.lowerNullableType() : ParameterDeclaration {
    if (type is TypeDeclaration) {
        return copy(type = (type as TypeDeclaration).lowerNullableType())
    } else if (type is FunctionTypeDeclaration) {
        return duplicate()
    } else {
        throw Exception("can not lower nullables for unknown param type ${type}")
    }
}

fun lowerNullable(node: DocumentRoot): DocumentRoot {

    val loweredDeclarations = node.declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> declaration.copy(type = declaration.type.lowerNullableType() as TypeDeclaration)
            is FunctionDeclaration -> declaration.copy(parameters = declaration.parameters.map { parameter ->
                if (parameter is ParameterDeclaration) {
                    parameter.lowerNullableType()
                } else {
                    throw Exception("can not lower nullables for unknown param type ${parameter}")
                }
            })
            else -> declaration.duplicate()
        }
    }

    return node.copy(declarations = loweredDeclarations)
}
