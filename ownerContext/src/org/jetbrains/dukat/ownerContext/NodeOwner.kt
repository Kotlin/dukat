package org.jetbrains.dukat.ownerContext

class NodeOwner<T>(val node: T, override val owner: NodeOwner<*>?) : OwnerContext
