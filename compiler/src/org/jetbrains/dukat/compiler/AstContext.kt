package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.tsmodel.HeritageSymbolDeclaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration


class AstContext {
    private val myInterfaces: MutableMap<HeritageSymbolDeclaration, InterfaceNode> = mutableMapOf()
    private val myClassNodes: MutableMap<HeritageSymbolDeclaration, ClassNode> = mutableMapOf()

    fun registerInterface(interfaceDeclaration: InterfaceNode) {
        myInterfaces.put(IdentifierDeclaration(interfaceDeclaration.name), interfaceDeclaration)
    }

    fun registerClass(classDeclaration: ClassNode) {
        myClassNodes.put(IdentifierDeclaration(classDeclaration.name), classDeclaration)
    }
    
    fun resolveInterface(name: HeritageSymbolDeclaration): InterfaceNode? = myInterfaces.get(name)

    fun resolveClass(name: HeritageSymbolDeclaration): ClassNode? {
        return myClassNodes.get(name)
    }
}