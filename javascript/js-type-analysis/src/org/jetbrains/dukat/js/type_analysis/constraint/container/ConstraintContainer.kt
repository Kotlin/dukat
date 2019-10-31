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

open class ConstraintContainer(protected val constraints: MutableSet<Constraint>) : Constraint {
    constructor(vararg params: Constraint) : this(mutableSetOf(*params))

    open operator fun plusAssign(constraint: Constraint) {
        constraints += constraint
    }

    open operator fun plusAssign(newConstraints: Collection<Constraint>) {
        constraints.addAll(newConstraints)
    }

    open fun copy() : ConstraintContainer {
        return ConstraintContainer(constraints)
    }

    open fun getFlatConstraints() : List<Constraint> {
        return constraints.flatMap { constraint ->
            if(constraint is ConstraintContainer) {
                constraint.getFlatConstraints()
            } else {
                listOf(constraint)
            }
        }
    }

    open fun resolveToType() : TypeDeclaration {
        val flatConstraints = getFlatConstraints()
        return when {
            flatConstraints.contains(NumberTypeConstraint) -> numberType
            flatConstraints.contains(BigIntTypeConstraint) -> numberType
            flatConstraints.contains(BooleanTypeConstraint) -> booleanType
            flatConstraints.contains(StringTypeConstraint) -> stringType
            else -> anyNullableType
        }
    }
}