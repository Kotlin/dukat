package org.jetbrains.dukat.js.type.constraint.reference

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.panic.raiseConcern

open class ReferenceConstraint(
        private val identifier: IdentifierEntity,
        owner: PropertyOwner
) : PropertyOwnerReferenceConstraint(owner) {
    private tailrec fun resolveInOwner(owner: PropertyOwner): Constraint? {
        val resolvedOwner = if(owner is Constraint) {
            val resolvedConstraint = owner.resolve()

            if (resolvedConstraint is PropertyOwner) {
                resolvedConstraint
            } else {
                raiseConcern("Accessing property of non-property-owner") { null }
            }
        } else {
            owner
        }

        return if(resolvedOwner != null) {
            val dereferencedConstraint = resolvedOwner[identifier]

            if (dereferencedConstraint != null && dereferencedConstraint !is ReferenceConstraint) {
                dereferencedConstraint.resolveWithProperties()
            } else {
                if (resolvedOwner.owner != null) {
                    resolveInOwner(resolvedOwner.owner!!)
                } else {
                    null
                }
            }
        } else {
            null
        }
    }

    override fun resolve(): Constraint {
        return resolveInOwner(owner) ?: CompositeConstraint()
    }
}