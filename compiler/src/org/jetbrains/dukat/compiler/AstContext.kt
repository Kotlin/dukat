package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.NameNode


class AstContext {
    private val myInterfaces: MutableMap<NameNode, InterfaceNode> = mutableMapOf()
    private val myClassNodes: MutableMap<NameNode, ClassNode> = mutableMapOf()

    fun registerInterface(interfaceDeclaration: InterfaceNode) {
        myInterfaces[IdentifierNode(interfaceDeclaration.name)] = interfaceDeclaration
    }

    fun registerClass(classDeclaration: ClassNode) {
        myClassNodes[IdentifierNode(classDeclaration.name)] = classDeclaration
    }

    fun resolveInterface(name: NameNode): InterfaceNode? = myInterfaces.get(name)

    fun resolveClass(name: NameNode): ClassNode? {
        return myClassNodes.get(name)
    }
}