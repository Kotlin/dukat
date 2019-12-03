package org.jetbrains.dukat.js.type.constraint

interface Constraint {
    operator fun plusAssign(other: Constraint)

    operator fun plusAssign(others: Collection<Constraint>) {
        others.forEach {
            this += it
        }
    }

    /**
     * Same as [resolve], but called instead,
     * when resolving a constraint to get a type
     * that can be used in a manner this constraint can.
     *
     * (For example, when passing an object to a
     * function, and adding an "x" property to it,
     * this resolution will not contain it, but
     * [resolve] will)
     */
    fun resolveAsInput() : Constraint

    /**
     * Resolves all references and other constraints it contains,
     * to turn this constraint into one that represents a type.
     *
     * Should only be run after collecting all constraints,
     * and constraint should not be modified afterwards.
     */
    fun resolve() : Constraint
}