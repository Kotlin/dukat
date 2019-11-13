package org.jetbrains.dukat.js.type.constraint.reference.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint

internal fun Constraint.getResolvedFunctionConstraint(owner: PropertyOwner) : FunctionConstraint? {
    return when (val resolvedCallTarget = this.resolve(owner)) {
        is FunctionConstraint -> resolvedCallTarget
        else -> null
    }
}