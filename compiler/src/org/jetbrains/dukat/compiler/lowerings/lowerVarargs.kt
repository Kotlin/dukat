package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.duplicate

private fun ParameterValue.extractVarargType(): ParameterValue? {
    if (this is TypeDeclaration) {
        if (value == "Array") {
            return params[0]
        } else if (value == "Any") {
            return this
        }
    }

    return null
}

private fun List<ParameterValue>.lowerVarargs() = map { paramValue -> paramValue.lowerVarargs() }

private fun ParameterValue.lowerVarargs(): ParameterValue {
    if (this is TypeDeclaration) {
        if (value == "@@Vararg") {
            params[0].extractVarargType()?.let {
                if (it is TypeDeclaration) {
                    return it.copy(vararg = true, params = it.params.lowerVarargs())
                } else if (it is FunctionTypeDeclaration) {
                    return it.copy(vararg = true)
                } else {
                    throw Exception("lowerVarargs called on unhandled type ${it}")
                }
            }
        }

        return copy(params = params.lowerVarargs())
    } else if (this is FunctionTypeDeclaration) {
        return copy(parameters = parameters.map { parameter -> parameter.copy(type = parameter.type.lowerVarargs()) })
    } else {
        throw Exception("lowerNativeArray called on unhandled type ${this}")
    }
}

private fun MemberDeclaration.lowerVarargs(): MemberDeclaration {
    if (this is FunctionDeclaration) {
        return copy(parameters = parameters.map { param -> param.copy(type = param.type.lowerVarargs()) })
    } else if (this is VariableDeclaration) {
        return copy(type = type.lowerVarargs())
    } else {
        throw Exception("failed to lowerVarargs for ${this}")
    }
}

fun DocumentRoot.lowerVarargs(): DocumentRoot {

    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> {
                declaration.copy(type = declaration.type.lowerVarargs())
            }
            is FunctionDeclaration -> {
                declaration.copy(parameters = declaration.parameters.map { param -> param.copy(type = param.type.lowerVarargs()) })
            }
            is ClassDeclaration -> declaration.copy(
                    members = declaration.members.map { member -> member.lowerVarargs() },
                    primaryConstructor = declaration.primaryConstructor?.let {it as FunctionDeclaration}
            )
            else -> declaration.duplicate()
        }
    }

    return copy(declarations = loweredDeclarations)
}