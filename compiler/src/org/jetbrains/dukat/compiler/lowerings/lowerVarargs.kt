package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.duplicate

private fun ParameterValue.extractVarargType() : ParameterValue? {
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


fun lowerVarargs(node: DocumentRoot): DocumentRoot {

    val loweredDeclarations = node.declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> {
                declaration.copy(type = declaration.type.lowerVarargs())
            }
            is FunctionDeclaration -> {
                declaration.copy(parameters = declaration.parameters.map {param -> param.copy(type = param.type.lowerVarargs())})
            }
            else -> declaration.duplicate()
        }
    }

    return node.copy(declarations = loweredDeclarations)
}