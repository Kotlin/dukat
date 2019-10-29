package org.jetbrains.dukat.js.type_analysis.constraint

import org.jetbrains.dukat.js.type_analysis.constraint.resolved.ResolvedConstraint

interface Constraint {
    fun resolve(): ResolvedConstraint
}