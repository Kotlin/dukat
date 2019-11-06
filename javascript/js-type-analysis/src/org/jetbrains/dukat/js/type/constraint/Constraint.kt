package org.jetbrains.dukat.js.type.constraint

import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner

interface Constraint {
    fun resolve(owner: PropertyOwner) : Constraint
}