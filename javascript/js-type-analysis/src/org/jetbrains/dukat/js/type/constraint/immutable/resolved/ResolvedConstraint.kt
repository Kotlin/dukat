package org.jetbrains.dukat.js.type.constraint.immutable.resolved

import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint

interface ResolvedConstraint : ImmutableConstraint {
    override fun resolve(resolveAsInput: Boolean) = this
}