package org.jetbrains.dukat.astCommon

interface Lowering<S, T> {
    fun lower(source: S): T
}