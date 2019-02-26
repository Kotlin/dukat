package org.jetbrains.dukat.ownerContext

interface OwnerContext {
    val owner: OwnerContext?

    fun getOwners() = generateSequence(this) {
        it.owner
    }

}