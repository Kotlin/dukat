package org.jetbrains.dukat.js.type.constraint.unresolved.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.container.ConstraintContainer
import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner
import org.jetbrains.dukat.js.type.constraint.unresolved.FunctionConstraint

data class CallArgumentConstraint(
        val callTarget: ConstraintContainer,
        val arguments: List<ConstraintContainer>,
        val argumentNum: Int
) : Constraint {
    override fun resolve(owner: PropertyOwner): Constraint {
        val functionConstraint = callTarget.getResolvedFunctionConstraint(owner)

        return if (functionConstraint != null && functionConstraint.parameterConstraints.size >= argumentNum) {
            return functionConstraint.parameterConstraints[argumentNum].second
        } else {
            ConstraintContainer()
        }
    }
}