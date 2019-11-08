package org.jetbrains.dukat.js.type.constraint.immutable.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

data class CallArgumentConstraint(
        val callTarget: Constraint,
        val arguments: List<Constraint>,
        val argumentNum: Int
) : ImmutableConstraint {
    override fun resolve(owner: PropertyOwner): Constraint {
        val functionConstraint = callTarget.getResolvedFunctionConstraint(owner)

        return if (functionConstraint != null && functionConstraint.parameterConstraints.size >= argumentNum) {
            return functionConstraint.parameterConstraints[argumentNum].second
        } else {
            CompositeConstraint()
        }
    }
}