package org.jetbrains.dukat.ast


fun translateType(declaration: TypeDeclaration): String {
    return when (declaration) {
        is SimpleTypeDeclaration -> {
            val res = mutableListOf<String>(declaration.value)
            if (declaration.isGeneric()) {
                val paramsList = mutableListOf<String>()
                for (param in declaration.params) {
                    paramsList.add(translateType(param))
                }
                res.add("<" + paramsList.joinToString(", ") + ">")
            }
            res.joinToString("")
        }
        else -> "@@UNKNOWN"
    }
}

fun lowerNativeArray(node: DocumentRoot): DocumentRoot {
    var declarations = mutableListOf<VariableDeclaration>()

    for (child in node.children()) {
        if (child is VariableDeclaration) {
            val type = child.type

            if (type is SimpleTypeDeclaration) {
                if (type.value == "@@ArraySugar") {
                    declarations.add(VariableDeclaration(child.name, SimpleTypeDeclaration("Array", type.params)))
                } else {
                    declarations.add(child.copy())
                }
            } else {
                throw Exception("Unkown ast type")
            }
        }
    }

    return DocumentRoot(declarations)
}


private fun findNullableType(type: SimpleTypeDeclaration): SimpleTypeDeclaration? {
    if (type.value != "@@Union") {
        return null
    }

    val params = type.params.filter {
        when (it is SimpleTypeDeclaration) {
            true -> when (it.value) {
                "undefined" -> false
                "null" -> false
                else -> true
            }
            else -> true
        }
    }

    if (params.size == 1) {
        return params[0] as SimpleTypeDeclaration
    } else {
        return null
    }
}


private fun lowerNullableType(node: VariableDeclaration, type: TypeDeclaration) : TypeDeclaration {
    if (type is SimpleTypeDeclaration) {
        val nullableType = findNullableType(type)
        if (nullableType != null) {
            return SimpleTypeDeclaration(
                    nullableType.value + "?",
                    nullableType.params.map { lowerNullableType(node, it.copy() as TypeDeclaration) }.toTypedArray()
            )
        } else {
            return SimpleTypeDeclaration(
                    type.value,
                    type.params.map { lowerNullableType(node, it.copy() as TypeDeclaration) }.toTypedArray()
            )
        }
    } else {
        throw Exception("Unknown AST type")
    }
}

private fun lowerNullable(node: DocumentRoot): DocumentRoot {
    var declarations = mutableListOf<VariableDeclaration>()

    for (child in node.children()) {
        if (child is VariableDeclaration) {
            val typeResolved = lowerNullableType(child, child.type)
            declarations.add(VariableDeclaration(child.name, typeResolved))
        }
    }

    return DocumentRoot(declarations)
}

fun compile(originalTree: AstTree) {

    var docRoot = lowerNativeArray(originalTree.root)
    docRoot = lowerNullable(docRoot)

    println("from compiler:")
    var res = mutableListOf<String>();

    for (child in docRoot.children()) {
        if (child is VariableDeclaration) {
            val declaration = child
            res.add("export var ${declaration.name}: ${translateType(declaration.type)}")
        }
    }

    print(res.joinToString("\n"))
}