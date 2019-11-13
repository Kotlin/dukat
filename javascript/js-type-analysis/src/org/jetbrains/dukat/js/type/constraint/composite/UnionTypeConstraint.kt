package org.jetbrains.dukat.js.type.constraint.composite

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class UnionTypeConstraint(
        val types: List<Constraint>
) : ImmutableConstraint {
    override fun resolve(owner: PropertyOwner): Constraint {
        return UnionTypeConstraint( types.map { it.resolve(owner) } )
    }
}