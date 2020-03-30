package org.jetbrains.dukat.astModel

interface CallableModel {
    val parameters: List<CallableParameterModel>
    val type: TypeModel
}