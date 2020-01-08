package org.jetbrains.dukat.js.type.constraint.composite

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.RecursiveConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.ThrowConstraint

class UnionTypeConstraint(
        val types: List<Constraint>
) : ImmutableConstraint {
    override fun resolve(resolveAsInput: Boolean): Constraint {
        val resolvedTypes = types.map { it.resolve(resolveAsInput) }.filter { it != RecursiveConstraint && it != ThrowConstraint }

        return when (resolvedTypes.size) {
            0 -> NoTypeConstraint
            1 -> resolvedTypes[0]
            else -> UnionTypeConstraint(resolvedTypes)
        }
    }
}
