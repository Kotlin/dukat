package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.MethodDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.PropertyDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.duplicate

private fun FunctionDeclaration.lowerNativeArray(): FunctionDeclaration {
    return copy(
        parameters = parameters.map { parameter -> parameter.copy(type = parameter.type.lowerNativeArray()) },
        typeParameters = typeParameters.map {typeParameter ->
                            typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> constraint.lowerNativeArray() })
                        }
    )
}

private fun MethodDeclaration.lowerNativeArray(): MethodDeclaration {
    return copy(
            parameters = parameters.map { parameter -> parameter.copy(type = parameter.type.lowerNativeArray()) },
            typeParameters = typeParameters.map {typeParameter ->
                typeParameter.copy(constraints = typeParameter.constraints.map { constraint -> constraint.lowerNativeArray() })
            }
    )
}

private fun VariableDeclaration.lowerNativeArray() : VariableDeclaration {
    return copy(type = type.lowerNativeArray())
}

private fun PropertyDeclaration.lowerNativeArray() : PropertyDeclaration {
    return copy(type = type.lowerNativeArray())
}

private fun List<ParameterValue>.lowerNativeArray() = map { param -> param.lowerNativeArray() }

private fun ParameterValue.lowerNativeArray(): ParameterValue {
    if (this is TypeDeclaration) {
        if (value == "@@ArraySugar") {
            return copy(value = "Array", params = params.lowerNativeArray())
        } else {
            return copy(params = params.lowerNativeArray())
        }
    } else if (this is FunctionTypeDeclaration) {
        return copy()
    } else {
        throw Exception("lowerNativeArray called on unhandled type ${this}")
    }
}

private fun MemberDeclaration.lowerNativeArray() : MemberDeclaration {
    if (this is MethodDeclaration) {
        return lowerNativeArray()
    } else if (this is VariableDeclaration) {
        return lowerNativeArray()
    } else if (this is PropertyDeclaration) {
        return lowerNativeArray()
    } else {
        throw Exception("can not lower member declaration ${this}")
    }
}

fun DocumentRoot.lowerNativeArray(): DocumentRoot {

    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> {
                declaration.copy(type = declaration.type.lowerNativeArray() )
            }
            is FunctionDeclaration -> {
                declaration.lowerNativeArray()
            }
            is ClassDeclaration -> {
                declaration.copy(
                        members = declaration.members.map { member -> member.lowerNativeArray() },
                        primaryConstructor = declaration.primaryConstructor?.lowerNativeArray()
                    )
            }
            is InterfaceDeclaration -> {
                declaration.copy(
                        members = declaration.members.map { member -> member.lowerNativeArray() }
                )
            }
            else -> declaration.duplicate()
        }
    }


    return copy(declarations = loweredDeclarations)
}
