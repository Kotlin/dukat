package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.RecursiveConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class FunctionConstraint(
        val returnConstraints: Constraint,
        val parameterConstraints: List<Pair<String, Constraint>>
) : ImmutableConstraint { //TODO make property owner (since functions technically are, in javascript)
    private var isResolving = false

    override fun resolve(owner: PropertyOwner): FunctionConstraint {
        if (!isResolving) {
            isResolving = true
            val resolvedConstraint = FunctionConstraint(
                    returnConstraints.resolve(owner),
                    parameterConstraints.map { (name, constraint) -> name to constraint.resolve(owner) }
            )
            isResolving = false
            return resolvedConstraint
        } else {
            return FunctionConstraint(
                    RecursiveConstraint,
                    parameterConstraints.map { (name, _) -> name to NoTypeConstraint }
            )
        }
    }
}