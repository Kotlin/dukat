package org.jetbrains.dukat.ownerContext

import org.jetbrains.dukat.astCommon.Entity

data class NodeOwner<T : Entity>(val node: T, override val owner: NodeOwner<*>?) : OwnerContext {
    fun <D: Entity> wrap(child: D): NodeOwner<D> = NodeOwner(child, this)
}

