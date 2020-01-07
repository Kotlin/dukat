package org.jetbrains.dukat.js.type.constraint.reference

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.constraint.resolution.ResolutionState
import org.jetbrains.dukat.js.type.propertyOwner.PropertyOwner
import org.jetbrains.dukat.panic.raiseConcern

open class ReferenceConstraint(
        private val identifier: IdentifierEntity,
        owner: PropertyOwner
) : PropertyOwnerReferenceConstraint(owner) {
    private var resolutionState = ResolutionState.UNRESOLVED
    private var resolvedConstraint: Constraint? = null

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

    override fun resolve(resolveAsInput: Boolean): Constraint {
        return when (resolutionState) {
            ResolutionState.UNRESOLVED -> {
                resolvedConstraint = resolveInOwner(owner) ?: CompositeConstraint(owner)
                resolutionState = ResolutionState.RESOLVED
                return resolvedConstraint!!
            }

            ResolutionState.RESOLVING -> {
                raiseConcern("Invalid converter state!") { CompositeConstraint(owner) }
            }

            ResolutionState.RESOLVED -> {
                resolvedConstraint!!
            }
        }
    }
}