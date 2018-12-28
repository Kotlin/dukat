package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.duplicate

private fun findNullableType(type: TypeDeclaration): TypeDeclaration? {
    if (type.value != "@@Union") {
        return null
    }

    val params = type.params.filter {
        when (it.value) {
            "undefined" -> false
            "null" -> false
            else -> true
        }
    }

    if (params.size == 1) {
        return params[0]
    } else {
        return null
    }
}

private fun lowerNullableType(type: TypeDeclaration): TypeDeclaration {
    val nullableType = findNullableType(type)

    if (nullableType != null) {
        return TypeDeclaration(
                nullableType.value + "?",
                nullableType.params.map { lowerNullableType(it.copy()) }.toTypedArray()
        )
    } else {
        return type.duplicate() as TypeDeclaration
    }
}


fun lowerNullable(node: DocumentRoot): DocumentRoot {

    val loweredDeclarations = node.declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> declaration.copy(type = lowerNullableType(declaration.type))
            is FunctionDeclaration -> declaration.copy(parameters = declaration.parameters.map { param ->
                param.copy(type = lowerNullableType(param.type))
            })
            else -> declaration.duplicate() as Declaration
        }
    }

    return node.copy(declarations = loweredDeclarations)
}
