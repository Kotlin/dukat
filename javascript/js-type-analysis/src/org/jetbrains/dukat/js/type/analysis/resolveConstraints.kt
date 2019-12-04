package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.js.type.property_owner.Scope

fun Scope.resolveConstraints() {
    propertyNames.forEach {
        this[it] = this[it].resolve()
    }
}
