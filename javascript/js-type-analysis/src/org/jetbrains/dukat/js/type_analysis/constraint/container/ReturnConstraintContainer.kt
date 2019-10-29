package org.jetbrains.dukat.js.type_analysis.constraint.container

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type_analysis.constraint.Constraint
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

class ReturnConstraintContainer(constraints: MutableSet<Constraint> = mutableSetOf()) : ConstraintContainer(constraints) {
    override fun copy(): ConstraintContainer {
        return ReturnConstraintContainer(constraints)
    }

    override fun resolveToType(): TypeDeclaration {
        return if(constraints.isEmpty()) {
            TypeDeclaration(
                    value = IdentifierEntity("Unit"),
                    params = emptyList(),
                    nullable = false
            )
        } else {
            TypeDeclaration(
                    value = IdentifierEntity("Any"),
                    params = emptyList(),
                    nullable = true
            )
        }
    }
}