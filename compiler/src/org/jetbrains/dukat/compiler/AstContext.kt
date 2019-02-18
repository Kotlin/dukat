package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.HeritageSymbolNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode


class AstContext {
    private val myInterfaces: MutableMap<HeritageSymbolNode, InterfaceNode> = mutableMapOf()
    private val myClassNodes: MutableMap<HeritageSymbolNode, ClassNode> = mutableMapOf()

    fun registerInterface(interfaceDeclaration: InterfaceNode) {
        myInterfaces.put(IdentifierNode(interfaceDeclaration.name), interfaceDeclaration)
    }

    fun registerClass(classDeclaration: ClassNode) {
        myClassNodes.put(IdentifierNode(classDeclaration.name), classDeclaration)
    }
    
    fun resolveInterface(name: HeritageSymbolNode): InterfaceNode? = myInterfaces.get(name)

    fun resolveClass(name: HeritageSymbolNode): ClassNode? {
        return myClassNodes.get(name)
    }
}