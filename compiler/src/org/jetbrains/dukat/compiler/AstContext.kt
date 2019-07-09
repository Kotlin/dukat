package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.astCommon.NameEntity


class AstContext {
    private val myInterfaces: MutableMap<NameEntity, InterfaceNode> = mutableMapOf()
    private val myClassNodes: MutableMap<NameEntity, ClassNode> = mutableMapOf()

    fun registerInterface(interfaceDeclaration: InterfaceNode) {
        myInterfaces[interfaceDeclaration.name] = interfaceDeclaration
    }

    fun registerClass(classDeclaration: ClassNode) {
        myClassNodes[classDeclaration.name] = classDeclaration
    }

    fun resolveInterface(name: NameEntity): InterfaceNode? = myInterfaces[name]

    fun resolveClass(name: NameEntity): ClassNode? {
        return myClassNodes.get(name)
    }
}