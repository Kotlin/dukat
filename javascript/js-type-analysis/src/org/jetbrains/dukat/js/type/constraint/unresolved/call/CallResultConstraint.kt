package org.jetbrains.dukat.js.type.constraint.unresolved.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.container.ConstraintContainer
import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner

data class CallResultConstraint(
        val callTarget: ConstraintContainer,
        val arguments: List<ConstraintContainer>
) : Constraint {
    override fun resolve(owner: PropertyOwner): Constraint {
        val functionConstraint = callTarget.getResolvedFunctionConstraint(owner)

        return functionConstraint?.returnConstraints ?: ConstraintContainer()
    }
}