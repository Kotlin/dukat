package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.js.type.propertyOwner.Scope

fun Scope.resolveConstraints() {
    propertyNames.forEach {
        this[it] = this[it].resolve()
    }
}
