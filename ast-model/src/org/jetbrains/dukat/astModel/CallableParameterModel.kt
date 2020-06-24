package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity

interface CallableParameterModel : Entity {
    val name: String?
    val type: TypeModel
}