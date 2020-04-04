package org.jetbrains.dukat.astModel

interface CanHaveExternalModifierModel : TopLevelModel {
    val external: Boolean
}