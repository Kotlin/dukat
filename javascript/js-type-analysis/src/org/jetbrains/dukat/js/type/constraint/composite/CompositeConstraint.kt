package org.jetbrains.dukat.js.type.constraint.composite

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.RecursiveConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.StringTypeConstraint

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

    override fun resolve(): Constraint {
        val resolvedConstraints = getFlatConstraints().map { it.resolve() }

        return when {
            resolvedConstraints.contains(NumberTypeConstraint) -> NumberTypeConstraint
            resolvedConstraints.contains(BigIntTypeConstraint) -> BigIntTypeConstraint
            resolvedConstraints.contains(BooleanTypeConstraint) -> BooleanTypeConstraint
            resolvedConstraints.contains(StringTypeConstraint) -> StringTypeConstraint
            resolvedConstraints.contains(RecursiveConstraint) -> RecursiveConstraint
            else -> NoTypeConstraint
        }
    }
}