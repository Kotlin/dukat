package org.jetbrains.dukat.js.type.constraint.immutable.resolved

import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

interface ResolvedConstraint : ImmutableConstraint {
    override fun resolve(owner: PropertyOwner) = this
}