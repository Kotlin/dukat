package org.jetbrains.dukat.js.type.constraint.reference.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint
import org.jetbrains.dukat.js.type.constraint.reference.PropertyOwnerReferenceConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class CallResultConstraint(
        owner: PropertyOwner,
        private val callTarget: Constraint
) : PropertyOwnerReferenceConstraint(owner) {
    override fun resolve(resolveAsInput: Boolean): Constraint {
        val functionConstraint = callTarget.resolve()

        return if (functionConstraint is FunctionConstraint) {
            functionConstraint.returnConstraints.resolveWithProperties()
        } else {
            CallResultConstraint(owner, functionConstraint)
        }
    }
}