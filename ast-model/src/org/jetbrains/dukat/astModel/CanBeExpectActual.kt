package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astModel.modifiers.ExpectActualModifier

interface CanBeExpectActual : Entity {
    val expectActualModifier: ExpectActualModifier? get() = null
}