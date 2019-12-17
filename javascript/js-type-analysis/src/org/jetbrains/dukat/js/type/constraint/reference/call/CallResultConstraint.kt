package org.jetbrains.dukat.js.type.constraint.reference.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.composite.UnionTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
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
            if (functionConstraint.versions.size == 1) {
                functionConstraint.versions[0].returnConstraints.resolveWithProperties()
            } else {
                UnionTypeConstraint(
                        functionConstraint.versions.map {
                            it.returnConstraints.resolveWithProperties()
                        }
                ).resolve()
            }
        } else {
            NoTypeConstraint
        }
    }
}