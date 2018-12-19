package org.jetbrains.dukat.ast.lowerings

import org.jetbrains.dukat.ast.*


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
    return parameters.map { parameter ->
        ParameterDeclaration(parameter.name, lowerType(parameter.type))
    }.toTypedArray()
}

fun lowerPrimitives(node: DocumentRoot): DocumentRoot {
    val loweredDeclarations = node.declarations.map { declaration -> when(declaration) {
        is VariableDeclaration -> VariableDeclaration(declaration.name, lowerType(declaration.type))
        is FunctionDeclaration -> FunctionDeclaration(declaration.name, lowerType(declaration.parameters.toTypedArray()), lowerType(declaration.type))
        else -> declaration.copy() as Declaration
    }}

    return node.copy(declarations = loweredDeclarations)
}
