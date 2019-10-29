package org.jetbrains.dukat.js.type_analysis.constraint.container

import org.jetbrains.dukat.js.type_analysis.constraint.Constraint
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

abstract class ConstraintContainer(protected val constraints: MutableSet<Constraint> = mutableSetOf()) {
    operator fun plusAssign(constraint: Constraint) {
        constraints += constraint
    }

    operator fun plusAssign(newConstraints: Collection<Constraint>) {
        constraints.addAll(newConstraints)
    }

    abstract fun copy() : ConstraintContainer

    abstract fun resolveToType() : TypeDeclaration
}