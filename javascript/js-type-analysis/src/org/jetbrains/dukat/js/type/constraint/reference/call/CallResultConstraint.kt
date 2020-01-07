package org.jetbrains.dukat.js.type.constraint.reference.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.composite.UnionTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint
import org.jetbrains.dukat.js.type.constraint.reference.PropertyOwnerReferenceConstraint
import org.jetbrains.dukat.js.type.propertyOwner.PropertyOwner

class CallResultConstraint(
        owner: PropertyOwner,
        private val callTarget: Constraint
) : PropertyOwnerReferenceConstraint(owner) {
    override fun resolve(resolveAsInput: Boolean): Constraint {
        val functionConstraint = callTarget.resolve()

        return if (functionConstraint is FunctionConstraint) {
            if (functionConstraint.overloads.size == 1) {
                functionConstraint.overloads[0].returnConstraints.resolveWithProperties()
            } else {
                UnionTypeConstraint(
                        functionConstraint.overloads.map {
                            it.returnConstraints.resolveWithProperties()
                        }
                ).resolve()
            }
        } else {
            NoTypeConstraint
        }
    }
}