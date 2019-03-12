package org.jetbrains.dukat.ownerContext

class NodeOwner<T>(val node: T, override val owner: NodeOwner<*>?) : OwnerContext {
    fun <D> wrap(child: D): NodeOwner<D> = NodeOwner(child, this)
}

