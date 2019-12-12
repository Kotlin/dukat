package org.jetbrains.dukat.js.type.constraint.immutable

import org.jetbrains.dukat.js.type.constraint.Constraint

interface ImmutableConstraint : Constraint {
    override operator fun plusAssign(other: Constraint) { /* do nothing */ }
}