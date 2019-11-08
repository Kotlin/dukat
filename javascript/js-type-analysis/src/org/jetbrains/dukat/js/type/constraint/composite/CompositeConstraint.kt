package org.jetbrains.dukat.js.type.constraint.composite

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.StringTypeConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import javax.lang.model.type.NoType

class CompositeConstraint(private val constraints: MutableSet<Constraint>) : Constraint {
    constructor(vararg constraints: Constraint) : this(mutableSetOf(*constraints))
    constructor(constraints: List<Constraint>) : this(constraints.toMutableSet())

    override operator fun plusAssign(other: Constraint) {
        constraints += other
    }

    override operator fun plusAssign(others: Collection<Constraint>) {
        constraints.addAll(others)
    }

    fun getFlatConstraints() : List<Constraint> {
        return constraints.flatMap { constraint ->
            if(constraint is CompositeConstraint) {
                constraint.getFlatConstraints()
            } else {
                listOf(constraint)
            }
        }
    }

    override fun resolve(owner: PropertyOwner): Constraint {
        val resolvedConstraints = getFlatConstraints().map { it.resolve(owner) }

        return when {
            resolvedConstraints.contains(NumberTypeConstraint) -> NumberTypeConstraint
            resolvedConstraints.contains(BigIntTypeConstraint) -> BigIntTypeConstraint
            resolvedConstraints.contains(BooleanTypeConstraint) -> BooleanTypeConstraint
            resolvedConstraints.contains(StringTypeConstraint) -> StringTypeConstraint
            else -> NoTypeConstraint
        }
    }
}