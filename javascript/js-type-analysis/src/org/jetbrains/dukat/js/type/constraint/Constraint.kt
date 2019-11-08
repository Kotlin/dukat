package org.jetbrains.dukat.js.type.constraint

import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

interface Constraint {
    operator fun plusAssign(other: Constraint)

    operator fun plusAssign(others: Collection<Constraint>) {
        others.forEach {
            this += it
        }
    }

    fun resolve(owner: PropertyOwner) : Constraint
}