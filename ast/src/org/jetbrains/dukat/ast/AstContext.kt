package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration

class AstContext {
    private val myInterfaces: MutableMap<String, InterfaceDeclaration> = mutableMapOf()
    private val myClassDeclarations: MutableMap<String, ClassDeclaration> = mutableMapOf()

    fun registerInterface(interfaceDeclaration: InterfaceDeclaration): InterfaceDeclaration {
        myInterfaces.put(interfaceDeclaration.name, interfaceDeclaration)
        return interfaceDeclaration
    }

    fun registerClass(classDeclaration: ClassDeclaration): ClassDeclaration {
        myClassDeclarations.put(classDeclaration.name, classDeclaration)
        return classDeclaration
    }

    fun resolveInterface(name: String): InterfaceDeclaration? = myInterfaces.get(name)

    fun resolveClass(name: String): ClassDeclaration? {
        return myClassDeclarations.get(name)
    }
}