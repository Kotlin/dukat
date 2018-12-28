package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.duplicate

private fun lowerType(type: TypeDeclaration): TypeDeclaration {
    return when (type.value) {
        "any" -> TypeDeclaration("Any", arrayOf())
        "boolean" -> TypeDeclaration("Boolean", arrayOf())
        "string" -> TypeDeclaration("String", arrayOf())

        "number" -> TypeDeclaration("Number", arrayOf())
        else -> type.copy()
    }
}

private fun lowerType(parameters: Array<ParameterDeclaration>): Array<ParameterDeclaration> {
    return parameters.map { parameter -> parameter.copy(type = lowerType(parameter.type))}.toTypedArray()
}

fun lowerPrimitives(node: DocumentRoot): DocumentRoot {
    val loweredDeclarations = node.declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> VariableDeclaration(declaration.name, lowerType(declaration.type))
            is FunctionDeclaration ->
                FunctionDeclaration(
                        declaration.name, lowerType(declaration.parameters.toTypedArray()), lowerType(declaration.type))
            else -> declaration.duplicate() as Declaration
        }
    }

    return node.copy(declarations = loweredDeclarations)
}
