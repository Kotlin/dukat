package org.jetbrains.dukat.js.type.constraint.reference.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class CallArgumentConstraint(
        private val callTarget: Constraint,
        private val argumentNum: Int
) : ImmutableConstraint {
    override fun resolve(owner: PropertyOwner): Constraint {
        val functionConstraint = callTarget.resolve(owner)

        return if (functionConstraint is FunctionConstraint) {
            if (functionConstraint.parameterConstraints.size >= argumentNum) {
                functionConstraint.parameterConstraints[argumentNum].second
            } else {
                CompositeConstraint()
            }
        } else {
            CallArgumentConstraint(functionConstraint, argumentNum)
        }
    }
}