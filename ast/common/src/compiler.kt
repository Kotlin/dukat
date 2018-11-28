package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.lowerings.lowerPrimitives


fun translateType(declaration: TypeDeclaration): String {
    val res = mutableListOf<String>(declaration.value)
    if (declaration.isGeneric()) {
        val paramsList = mutableListOf<String>()
        for (param in declaration.params) {
            paramsList.add(translateType(param))
        }
        res.add("<" + paramsList.joinToString(", ") + ">")
    }
    return res.joinToString("")
}

fun lowerNativeArray(node: DocumentRoot): DocumentRoot {

    return node.copy { declaration ->
        when (declaration) {
            is VariableDeclaration -> {
                if (declaration.type.value == "@@ArraySugar") {
                    VariableDeclaration(declaration.name, TypeDeclaration("Array", declaration.type.params))
                } else null
            }
            is FunctionDeclaration -> null
            else -> throw Exception("Unknown AST type")
        }
    }
}


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


private fun lowerNullableType(node: VariableDeclaration, type: TypeDeclaration) : TypeDeclaration {
    val nullableType = findNullableType(type)

    if (nullableType != null) {
        return TypeDeclaration(
                nullableType.value + "?",
                nullableType.params.map { lowerNullableType(node, it.copy()) }.toTypedArray()
        )
    } else {
        return type.copy()
    }
}

private fun lowerNullable(node: DocumentRoot): DocumentRoot {
    val nodeCopy = node.copy { child ->
        when (child) {
            is VariableDeclaration -> VariableDeclaration(child.name, lowerNullableType(child, child.type))
            else -> null
        }
    }
    return nodeCopy
}


fun compile(originalTree: AstTree): String {
    var docRoot = lowerNativeArray(originalTree.root)
    docRoot = lowerNullable(docRoot)
    docRoot = lowerPrimitives(docRoot)

    var res = mutableListOf<String>();

    for (child in docRoot.children()) {
        if (child is VariableDeclaration) {
            val declaration = child
            res.add("export var ${declaration.name}: ${translateType(declaration.type)}")
        } else if (child is FunctionDeclaration) {
            val declaration = child
            var params = declaration.parameters.map { it.name + ": " + translateType(it.type) }.joinToString(", ")
            var returnType = translateType(child.type)
            res.add("external fun ${declaration.name}(${params}): ${returnType} = definedExternally")
        }
    }

    return res.joinToString("\n")
}

fun compile(fileName: String, translator: (fileName: String) -> AstTree): String {
    return compile(translator(fileName))
}