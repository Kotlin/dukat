package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity

interface NamedCallableModel : CallableModel<ParameterModel>, NamedModel {
    override val name: NameEntity
}