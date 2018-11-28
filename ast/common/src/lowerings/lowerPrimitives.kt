package org.jetbrains.dukat.ast.lowerings

import org.jetbrains.dukat.ast.*


private fun lowerType(type: TypeDeclaration): TypeDeclaration {
    return when (type.value) {
        "any" -> TypeDeclaration("Any", arrayOf())
        "boolean" -> TypeDeclaration("Boolean", arrayOf())
        "string" -> TypeDeclaration("String", arrayOf())
        else -> type.copy()
    }
}

private fun lowerType(parameters: Array<ParameterDeclaration>): Array<ParameterDeclaration> {
    return parameters.map { parameter ->
        ParameterDeclaration(parameter.name, lowerType(parameter.type))
    }.toTypedArray()
}

fun lowerPrimitives(node: DocumentRoot): DocumentRoot {
    return node.copy { declaration ->
        when (declaration) {
            is VariableDeclaration -> VariableDeclaration(declaration.name, lowerType(declaration.type))

            //TODO: here we actually need to copy parameters - this is something I don't like about current implementation
            is FunctionDeclaration -> FunctionDeclaration(declaration.name, lowerType(declaration.parameters), lowerType(declaration.type))
            else -> null
        }
    }
}
