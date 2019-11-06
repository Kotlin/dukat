package org.jetbrains.dukat.js.type.constraint.container

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner

class ConstraintContainer(protected val constraints: MutableSet<Constraint>) : Constraint {
    constructor(vararg constraints: Constraint) : this(mutableSetOf(*constraints))
    constructor(constraints: List<Constraint>) : this(constraints.toMutableSet())

    operator fun plusAssign(constraint: Constraint) {
        constraints += constraint
    }

    operator fun plusAssign(newConstraints: Collection<Constraint>) {
        constraints.addAll(newConstraints)
    }

    fun copy() : ConstraintContainer {
        return ConstraintContainer(constraints)
    }

    fun getFlatConstraints() : List<Constraint> {
        return constraints.flatMap { constraint ->
            if(constraint is ConstraintContainer) {
                constraint.getFlatConstraints()
            } else {
                listOf(constraint)
            }
        }
    }

    override fun resolve(owner: PropertyOwner): Constraint {
        return ConstraintContainer(getFlatConstraints().map { it.resolve(owner) })
    }
}