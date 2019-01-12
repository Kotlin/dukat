package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
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

fun lowerNativeArray(node: DocumentRoot): DocumentRoot {

    val loweredDeclarations = node.declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> {
                declaration.copy(type = declaration.type.lowerNativeArray() )
            }
            is FunctionDeclaration -> {
                declaration.lowerNativeArray()
            }
            else -> declaration.duplicate()
        }
    }


    return node.copy(declarations = loweredDeclarations)
}
