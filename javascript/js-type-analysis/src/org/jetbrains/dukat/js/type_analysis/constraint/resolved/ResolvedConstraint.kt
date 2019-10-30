package org.jetbrains.dukat.js.type_analysis.constraint.resolved

import org.jetbrains.dukat.js.type_analysis.constraint.Constraint

abstract class ResolvedConstraint : Constraint {
    override fun resolve() = this
}