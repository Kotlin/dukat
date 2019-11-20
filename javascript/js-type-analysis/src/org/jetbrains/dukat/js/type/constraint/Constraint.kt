package org.jetbrains.dukat.js.type.constraint

interface Constraint {
    operator fun plusAssign(other: Constraint)

    operator fun plusAssign(others: Collection<Constraint>) {
        others.forEach {
            this += it
        }
    }

    fun resolve() : Constraint
}