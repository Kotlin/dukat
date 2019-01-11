package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.duplicate


private fun List<ParameterValue>.lowerType() = map { param -> param.lowerType() }

private fun TypeDeclaration.lowerType(): TypeDeclaration {
    return when(value) {
        "any" -> copy(value = "Any", params = params.lowerType())
        "boolean" -> copy(value = "Boolean", params = params.lowerType())
        "string" -> copy(value = "String", params = params.lowerType())
        "number" -> copy(value = "Number", params = params.lowerType())
        else -> copy(params = params.lowerType())
    }
}

private fun ParameterValue.lowerType() : ParameterValue {
    if (this is TypeDeclaration) {
        return  lowerType()
    } else if (this is FunctionTypeDeclaration) {
        return copy(
            parameters = parameters.map { param -> param.lowerType() },
            type = type.lowerType()
        )
    } else {
        throw Exception("can not lower primitivies for unknown param type ${this}")
    }
}

private fun ParameterDeclaration.lowerType() : ParameterDeclaration {
   return copy(type = type.lowerType())
}

fun lowerPrimitives(node: DocumentRoot): DocumentRoot {
    val loweredDeclarations = node.declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> VariableDeclaration(declaration.name, declaration.type.lowerType())
            is FunctionDeclaration ->
                declaration.copy(
                        parameters = declaration.parameters.map { param -> param.lowerType() },
                        type = declaration.type.lowerType()
                    )
            else -> declaration.duplicate()
        }
    }

    return node.copy(declarations = loweredDeclarations)
}
