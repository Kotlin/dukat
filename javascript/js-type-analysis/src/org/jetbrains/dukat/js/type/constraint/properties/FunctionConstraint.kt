package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.RecursiveConstraint
import org.jetbrains.dukat.js.type.constraint.resolution.ResolutionState

class FunctionConstraint(
        val returnConstraints: Constraint,
        val parameterConstraints: List<Pair<String, Constraint>>
) : ImmutableConstraint { //TODO make property owner (since functions technically are, in javascript)
    private var resolutionState = ResolutionState.UNRESOLVED
    private var resolvedConstraint: FunctionConstraint? = null

    override fun resolve(): FunctionConstraint {
        return when (resolutionState) {
            ResolutionState.UNRESOLVED -> {
                resolutionState = ResolutionState.RESOLVING
                resolvedConstraint = FunctionConstraint(
                        returnConstraints.resolve(),
                        parameterConstraints.map { (name, constraint) -> name to constraint.resolve() }
                )
                resolutionState = ResolutionState.RESOLVED

                resolvedConstraint!!
            }

            ResolutionState.RESOLVING -> {
                FunctionConstraint(
                        RecursiveConstraint,
                        parameterConstraints.map { (name, _) -> name to NoTypeConstraint }
                )
            }

            ResolutionState.RESOLVED -> {
                resolvedConstraint!!
            }
        }
    }
}