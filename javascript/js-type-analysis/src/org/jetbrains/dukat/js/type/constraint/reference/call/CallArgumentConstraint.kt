package org.jetbrains.dukat.js.type.constraint.reference.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class CallArgumentConstraint(
        private val owner: PropertyOwner,
        private val callTarget: Constraint,
        private val argumentNum: Int
) : ImmutableConstraint {
    override fun resolve(resolveAsInput: Boolean): Constraint {
        val functionConstraint = callTarget.resolve()

        return if (functionConstraint is FunctionConstraint && functionConstraint.parameterConstraints.size > argumentNum) {
            functionConstraint.parameterConstraints[argumentNum].second.resolve(resolveAsInput = true)
        } else {
            CompositeConstraint(owner)
        }
    }
}