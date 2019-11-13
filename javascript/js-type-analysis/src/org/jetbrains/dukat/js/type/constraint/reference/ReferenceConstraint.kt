package org.jetbrains.dukat.js.type.constraint.reference

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.constraint.properties.PropertyOwnerConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.panic.raiseConcern

open class ReferenceConstraint(
        private val identifier: IdentifierEntity,
        private val parent: PropertyOwnerConstraint? = null
) : PropertyOwnerReferenceConstraint() {
    override fun resolve(owner: PropertyOwner): Constraint {
        val referenceOwner = if (parent != null) {
            val resolvedParent = parent.resolve(owner)

            if(resolvedParent is PropertyOwner) {
                resolvedParent
            } else {
                raiseConcern("Accessing property of non-property-owner") {  }
                return CompositeConstraint()
            }
        } else {
            owner
        }

        println(identifier.value)
        val dereferencedConstraint = referenceOwner[identifier]

        return if (dereferencedConstraint != null && dereferencedConstraint !is ReferenceConstraint) {
            dereferencedConstraint.resolveWithProperties(referenceOwner)
        } else {
            this
        }
    }
}