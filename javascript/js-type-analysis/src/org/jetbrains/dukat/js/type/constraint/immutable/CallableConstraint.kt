package org.jetbrains.dukat.js.type.constraint.immutable

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.resolution.ResolutionState

class CallableConstraint(
        parameterConstraints: List<Constraint>,
        returnConstraints: Constraint
) : ImmutableConstraint {
    var parameterConstraints = parameterConstraints
        private set

    var returnConstraints = returnConstraints
        private set

    private var resolutionState: ResolutionState = ResolutionState.UNRESOLVED

    override fun resolve(resolveAsInput: Boolean): Constraint {
        if (resolutionState == ResolutionState.UNRESOLVED) {
            resolutionState = ResolutionState.RESOLVING
            parameterConstraints = parameterConstraints.map { it.resolve(resolveAsInput = true) }
            returnConstraints = returnConstraints.resolve(resolveAsInput)
            resolutionState = ResolutionState.RESOLVED
        }

        return this
    }
}
