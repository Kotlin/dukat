package org.jetbrains.dukat.js.type.constraint.immutable

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.resolution.ResolutionState

class CallableConstraint(
        val parameterCount: Int,
        returnConstraints: Constraint
) : ImmutableConstraint {
    var returnConstraints = returnConstraints
        private set

    private var resolutionState: ResolutionState = ResolutionState.UNRESOLVED

    override fun resolve(resolveAsInput: Boolean): Constraint {
        if (resolutionState == ResolutionState.UNRESOLVED) {
            resolutionState = ResolutionState.RESOLVING
            returnConstraints = returnConstraints.resolve(resolveAsInput)
            resolutionState = ResolutionState.RESOLVED
        }

        return this
    }
}
