package org.jetbrains.dukat.js.type.constraint.immutable.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.VoidTypeConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

data class CallResultConstraint(
        val callTarget: Constraint,
        val arguments: List<Constraint>
) : ImmutableConstraint { //TODO treat this similarly to a reference constraint
    override fun resolve(owner: PropertyOwner): Constraint {
        val functionConstraint = callTarget.getResolvedFunctionConstraint(owner)

        return functionConstraint?.returnConstraints ?: VoidTypeConstraint
    }
}