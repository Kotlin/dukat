package org.jetbrains.dukat.js.type.constraint.reference.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.VoidTypeConstraint
import org.jetbrains.dukat.js.type.constraint.reference.PropertyOwnerReferenceConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class CallResultConstraint(
        private val callTarget: Constraint
) : PropertyOwnerReferenceConstraint() {
    override fun resolve(owner: PropertyOwner): Constraint {
        val resultConstraint = callTarget.getResolvedFunctionConstraint(owner)?.returnConstraints ?: VoidTypeConstraint
        return resultConstraint.resolveWithProperties(owner)
    }
}