package org.jetbrains.dukat.astModel

interface ParametersOwnerModel<T : CallableParameterModel> {
    val parameters: List<T>
}