package org.jetbrains.dukat.js.type_analysis.constraint.container

import org.jetbrains.dukat.js.type_analysis.constraint.Constraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type_analysis.constraint.resolved.StringTypeConstraint
import org.jetbrains.dukat.js.type_analysis.type.anyNullableType
import org.jetbrains.dukat.js.type_analysis.type.booleanType
import org.jetbrains.dukat.js.type_analysis.type.numberType
import org.jetbrains.dukat.js.type_analysis.type.stringType
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

open class ConstraintContainer(protected val constraints: MutableSet<Constraint> = mutableSetOf()) {
    operator fun plusAssign(constraint: Constraint) {
        constraints += constraint
    }

    operator fun plusAssign(newConstraints: Collection<Constraint>) {
        constraints.addAll(newConstraints)
    }

    open fun copy() : ConstraintContainer {
        return ConstraintContainer(constraints)
    }

    open fun resolveToType() : TypeDeclaration {
        return when {
            constraints.contains(NumberTypeConstraint) -> numberType
            constraints.contains(BigIntTypeConstraint) -> numberType
            constraints.contains(BooleanTypeConstraint) -> booleanType
            constraints.contains(StringTypeConstraint) -> stringType
            else -> anyNullableType
        }
    }
}