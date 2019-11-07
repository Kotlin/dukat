package org.jetbrains.dukat.js.type.constraint.unresolved.call

import org.jetbrains.dukat.js.type.constraint.container.ConstraintContainer
import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner
import org.jetbrains.dukat.js.type.constraint.unresolved.FunctionConstraint

internal fun ConstraintContainer.getResolvedFunctionConstraint(owner: PropertyOwner) : FunctionConstraint? {
    return when (val resolvedCallTarget = this.resolve(owner)) {
        is ConstraintContainer -> {
            val callTargetConstraints = resolvedCallTarget.getFlatConstraints()
            return callTargetConstraints.firstOrNull { it is FunctionConstraint } as FunctionConstraint?
        }
        is FunctionConstraint -> resolvedCallTarget
        else -> null
    }
}