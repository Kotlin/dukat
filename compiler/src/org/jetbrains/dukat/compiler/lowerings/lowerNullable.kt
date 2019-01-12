package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.duplicate

private fun FunctionTypeDeclaration.lowerNullableType(nullable: Boolean = false) = copy(
        nullable = nullable,
        parameters =  parameters.map { parameter ->
            parameter.lowerNullableType()
        },
        type = type.lowerNullableType()
)


private fun TypeDeclaration.lowerNullableType(nullable: Boolean = false) = copy(
    nullable = nullable,
    params = params.map { it.lowerNullableType() }
)

private fun ParameterValue.lowerNullableType(): ParameterValue {
    return when (this) {
        is TypeDeclaration -> {
            if (value == "@@Union") {
                val params = params.filter { param ->
                    param != TypeDeclaration("undefined", emptyList()) &&
                            param != TypeDeclaration("null", emptyList())
                }

                if (params.size == 1) {
                    val nullable =  params[0]
                    if (nullable is TypeDeclaration) {
                        return nullable.lowerNullableType(true)
                    } else if (nullable is FunctionTypeDeclaration) {
                        return nullable.lowerNullableType(true)
                    } else {
                        throw Exception("can not lower nullables for unknown param type ${nullable}")
                    }
                } else lowerNullableType()
            } else {
                lowerNullableType()
            }
        }
        is FunctionTypeDeclaration -> {
            lowerNullableType()
        }
        else -> throw Exception("can not lower nullables for unknown param type ${this}")
    }
}


private fun ParameterDeclaration.lowerNullableType(): ParameterDeclaration {
    if (type is TypeDeclaration) {
        return copy(type = type.lowerNullableType())
    } else if (type is FunctionTypeDeclaration) {
        val functionTypeDeclaration = type as FunctionTypeDeclaration
        return copy(type = functionTypeDeclaration.copy(
                parameters = functionTypeDeclaration.parameters.map { parameter -> parameter.lowerNullableType() },
                type = functionTypeDeclaration.type.lowerNullableType()
        ))
    } else {
        throw Exception("can not lower nullables for unknown param type ${type}")
    }
}

fun lowerNullable(node: DocumentRoot): DocumentRoot {

    val loweredDeclarations = node.declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> declaration.copy(type = declaration.type.lowerNullableType())
            is FunctionDeclaration -> declaration.copy(
                    parameters = declaration.parameters.map { parameter ->
                        parameter.lowerNullableType()
                    },
                    type = declaration.type.lowerNullableType()
            )
            else -> declaration.duplicate()
        }
    }

    return node.copy(declarations = loweredDeclarations)
}
