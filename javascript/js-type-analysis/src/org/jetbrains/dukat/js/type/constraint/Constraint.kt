package org.jetbrains.dukat.js.type.constraint

interface Constraint {
    operator fun plusAssign(other: Constraint)

    operator fun plusAssign(others: Collection<Constraint>) {
        others.forEach {
            this += it
        }
    }

    /**
     * Resolves all references and other constraints it contains,
     * to turn this constraint into one that represents a type.
     *
     * Should only be run after collecting all constraints,
     * and constraint should not be modified afterwards.
     *
     * When resolving as an input, resulting constraint
     * won't contain properties added to this constraint.
     */
    fun resolve(resolveAsInput: Boolean = false): Constraint
}