package org.jetbrains.dukat.js.type.constraint.reference.call

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.constraint.composite.UnionTypeConstraint
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint
import org.jetbrains.dukat.js.type.propertyOwner.PropertyOwner

class CallArgumentConstraint(
        private val owner: PropertyOwner,
        private val callTarget: Constraint,
        private val argumentNum: Int
) : ImmutableConstraint {
    override fun resolve(resolveAsInput: Boolean): Constraint {
        val functionConstraint = callTarget.resolve()

        if (functionConstraint is FunctionConstraint) {
            if (functionConstraint.overloads.size == 1) {
                val parameters = functionConstraint.overloads[0].parameterConstraints

                if (parameters.size > argumentNum) {
                    return parameters[argumentNum].second.resolve(resolveAsInput = true)
                }
            } else {
                return UnionTypeConstraint(
                        functionConstraint.overloads.mapNotNull {
                            if (it.parameterConstraints.size > argumentNum) {
                                it.parameterConstraints[argumentNum].second.resolve(resolveAsInput = true)
                            } else null
                        }
                ).resolve()
            }
        }

        return CompositeConstraint(owner)
    }
}