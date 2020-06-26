package org.jetbrains.dukat.astModel

interface CallableModel<T : CallableParameterModel> {
    val parameters: List<T>
    val type: TypeModel
}