package org.jetbrains.dukat.astModel

interface CallableModel<T : CallableParameterModel> : ParametersOwnerModel<T> {
    override val parameters: List<T>
    val type: TypeModel
}