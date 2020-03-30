package org.jetbrains.dukat.astModel

interface CallableParameterModel {
    val name: String?
    val type: TypeModel
}