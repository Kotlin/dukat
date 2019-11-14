package org.jetbrains.dukat.js.type.constraint.composite

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.RecursiveConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class UnionTypeConstraint(
        val types: List<Constraint>
) : ImmutableConstraint {
    override fun resolve(owner: PropertyOwner): Constraint {
        val resolvedTypes = types.map { it.resolve(owner) }.filter { it != RecursiveConstraint }

        return when (resolvedTypes.size) {
            0 -> NoTypeConstraint
            1 -> resolvedTypes[0]
            else -> UnionTypeConstraint(resolvedTypes)
        }
    }
}