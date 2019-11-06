package org.jetbrains.dukat.js.type.constraint.resolved

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner

interface ResolvedConstraint : Constraint {
    override fun resolve(owner: PropertyOwner) = this
}