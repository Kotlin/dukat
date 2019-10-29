package org.jetbrains.dukat.js.type_analysis.constraint.resolved

import org.jetbrains.dukat.js.type_analysis.constraint.Constraint

interface ResolvedConstraint : Constraint {
    override fun resolve() = this

    val typeName: String
}