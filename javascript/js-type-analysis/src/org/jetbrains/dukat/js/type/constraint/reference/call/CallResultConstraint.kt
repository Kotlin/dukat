package org.jetbrains.dukat.js.type.constraint.reference.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint
import org.jetbrains.dukat.js.type.constraint.reference.PropertyOwnerReferenceConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class CallResultConstraint(
        private val callTarget: Constraint
) : PropertyOwnerReferenceConstraint() {
    override fun resolve(owner: PropertyOwner): Constraint {
        val functionConstraint = callTarget.resolve(owner)

        return if (functionConstraint is FunctionConstraint) {
            functionConstraint.returnConstraints.resolveWithProperties(owner)
        } else {
            CallResultConstraint(functionConstraint)
        }
    }
}