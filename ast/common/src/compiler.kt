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
                println(paramsList)
                res.add("<" +paramsList.joinToString(", ") + ">")
            }
            res.joinToString("")
        }
        else -> "@@UNKNOWN"
    }
}

fun compile(tree: AstTree) {

    println("from compiler:")
    var res = mutableListOf<String>();
    for (declaration in tree.root.declarations) {
        res.add("export var ${declaration.name}: ${translateType(declaration.type)}")
    }

    print(res.joinToString("\n"))

}