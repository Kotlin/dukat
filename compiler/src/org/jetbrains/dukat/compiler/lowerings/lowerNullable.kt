package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.MethodDeclaration
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

private fun MemberDeclaration.lowerNullable() : MemberDeclaration {
    if (this is MethodDeclaration) {
        return lowerNullable()
    } else if (this is VariableDeclaration) {
        return lowerNullable()
    } else {
        throw Exception("can not null member declaration ${this}")
    }
}

private fun FunctionDeclaration.lowerNullable() = copy(
        parameters = parameters.map { parameter ->
            parameter.lowerNullableType()
        },
        type = type.lowerNullableType()
)

private fun MethodDeclaration.lowerNullable() = copy(
        parameters = parameters.map { parameter ->
            parameter.lowerNullableType()
        },
        type = type.lowerNullableType()
)


private fun VariableDeclaration.lowerNullable() = copy(type = type.lowerNullableType())

fun DocumentRoot.lowerNullable(): DocumentRoot {

    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> declaration.lowerNullable()
            is FunctionDeclaration -> declaration.lowerNullable()
            is ClassDeclaration -> declaration.copy(
                    members = declaration.members.map { member -> member.lowerNullable() },
                    primaryConstructor = declaration.primaryConstructor?.lowerNullable()
            )
            else -> declaration.duplicate()
        }
    }

    return copy(declarations = loweredDeclarations)
}
