package org.jetbrains.dukat.js.type_analysis.constraint.container

import org.jetbrains.dukat.js.type_analysis.constraint.Constraint
import org.jetbrains.dukat.js.type_analysis.type.unitType
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

class ReturnConstraintContainer(constraints: MutableSet<Constraint>) : ConstraintContainer(constraints) {
    constructor(vararg params: Constraint) : this(mutableSetOf(*params))

    override fun copy(): ConstraintContainer {
        return ReturnConstraintContainer(constraints)
    }

    override fun resolveToType(): TypeDeclaration {
        return if(getFlatConstraints().isEmpty()) {
            unitType
        } else {
            super.resolveToType()
        }
    }
}