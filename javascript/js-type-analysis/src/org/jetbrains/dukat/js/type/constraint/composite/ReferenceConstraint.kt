package org.jetbrains.dukat.js.type.constraint.composite

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

data class ReferenceConstraint(
        val identifier: IdentifierEntity
) : ImmutableConstraint { //TODO make modifiable
    override fun resolve(owner: PropertyOwner): Constraint {
        return owner[identifier]?.resolve(owner) ?: this
    }
}