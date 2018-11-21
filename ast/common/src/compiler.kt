package org.jetbrains.dukat.ast

fun compile(tree: AstTree) {

    println("from compiler:")
    for (declaration in tree.root.declarations) {

        if (declaration.type is SimpleTypeDeclaration) {
            println("export var ${declaration.name}: ${declaration.type.value}")
        } else {
            println("export var ${declaration.name}: NON ATOMIC")
        }
    }

}