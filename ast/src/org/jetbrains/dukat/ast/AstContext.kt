package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.InterfaceDeclaration

class AstContext {
    private val myInterfaces: MutableMap<String, InterfaceDeclaration> = mutableMapOf()

    fun registerInterface(interfaceDeclaration: InterfaceDeclaration): InterfaceDeclaration {
        myInterfaces.put(interfaceDeclaration.name, interfaceDeclaration)
        return interfaceDeclaration
    }

    fun resolveInterface(name: String): InterfaceDeclaration? {
        val res = myInterfaces.get(name)
        return res
    }
}