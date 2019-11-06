package org.jetbrains.dukat.js.type.constraint.resolution

private var uid = 0;

internal fun getUID() : String {
    return uid++.toString()
}