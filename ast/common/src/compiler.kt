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

fun lowerNativeArray(tree: AstTree): AstTree {
    var declarations = mutableListOf<VariableDeclaration>()

    for (child in tree.root.children()) {
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

    return AstTree(DocumentRoot(declarations))
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

private fun lowerNullable(tree: AstTree): AstTree {
    var declarations = mutableListOf<VariableDeclaration>()

    for (child in tree.root.children()) {
        if (child is VariableDeclaration) {
            val type = child.type

            if (type is SimpleTypeDeclaration) {
                val nullableType = findNullableType(type)
                if (nullableType != null) {
                    declarations.add(VariableDeclaration(child.name, SimpleTypeDeclaration(nullableType.value + "?", nullableType.params)))
                } else {
                    declarations.add(child.copy())
                }
            } else {
                throw Exception("Unkown ast type")
            }
        }
    }

    return AstTree(DocumentRoot(declarations))
}

fun compile(originalTree: AstTree) {

    var tree = lowerNativeArray(originalTree)
    tree = lowerNullable(tree)

    println("from compiler:")
    var res = mutableListOf<String>();
    for (declaration in tree.root.declarations) {
        res.add("export var ${declaration.name}: ${translateType(declaration.type)}")
    }

    print(res.joinToString("\n"))
}