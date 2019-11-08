package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class FunctionConstraint(
        val returnConstraints: Constraint,
        val parameterConstraints: List<Pair<String, Constraint>>
) : ImmutableConstraint { //TODO make property owner (since functions technically are, in javascript)
    override fun resolve(owner: PropertyOwner): Constraint {
        return FunctionConstraint(
                returnConstraints.resolve(owner),
                parameterConstraints.map { (name, constraint) -> name to constraint.resolve(owner) }
        )
    }
}