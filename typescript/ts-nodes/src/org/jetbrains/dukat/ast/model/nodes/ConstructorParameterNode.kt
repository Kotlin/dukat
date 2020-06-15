package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity

interface ConstructorParameterNode : Entity {
    val name: String
    val type: TypeNode
    val initializer: TypeValueNode?
}

fun ConstructorParameterNode.changeType(newType: TypeNode): ConstructorParameterNode {
    return when (this) {
        is ParameterNode -> copy(type = newType)
        is PropertyParameterNode -> copy(type = newType)
        else -> this
    }
}

fun ConstructorParameterNode.changeName(newName: String): ConstructorParameterNode {
    return when (this) {
        is ParameterNode -> copy(name = newName)
        is PropertyParameterNode -> copy(name = newName)
        else -> this
    }
}
