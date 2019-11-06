package org.jetbrains.dukat.js.type.constraint.unresolved

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner

data class ReferenceConstraint(
        val identifier: IdentifierEntity
) : Constraint {
    override fun resolve(owner: PropertyOwner): Constraint {
        return owner[identifier] ?: this
    }
}