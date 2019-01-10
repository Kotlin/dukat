package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.duplicate


private fun lower(value: String): TypeDeclaration? {
    return when(value) {
        "any" -> TypeDeclaration("Any", arrayOf())
        "boolean" -> TypeDeclaration("Boolean", arrayOf())
        "string" -> TypeDeclaration("String", arrayOf())
        "number" -> TypeDeclaration("Number", arrayOf())
        else -> null
    }
}

private fun ParameterValue.lowerType() : ParameterValue {
    if (this is TypeDeclaration) {
        return  lower(value) ?: copy()
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

private fun FunctionDeclaration.lowerParamsType() : List<ParameterDeclaration> {
    return parameters.map { parameter ->
        if (parameter is ParameterDeclaration) {
            parameter.lowerType()
        } else {
            throw Exception("can not lower primitivies for unknown param type ${parameter}")
        }
    }
}

fun lowerPrimitives(node: DocumentRoot): DocumentRoot {
    val loweredDeclarations = node.declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> VariableDeclaration(declaration.name, declaration.type.lowerType() as TypeDeclaration)
            is FunctionDeclaration ->
                FunctionDeclaration(
                        declaration.name, declaration.lowerParamsType(), if (declaration.type is TypeDeclaration) {
                    (declaration.type as TypeDeclaration).lowerType()
                } else {throw Exception("can not lowe primitive ${declaration}")})
            else -> declaration.duplicate()
        }
    }

    return node.copy(declarations = loweredDeclarations)
}
