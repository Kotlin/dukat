package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

interface PropertyOwnerConstraint : PropertyOwner, Constraint {
    override fun plusAssign(other: Constraint) {}
}