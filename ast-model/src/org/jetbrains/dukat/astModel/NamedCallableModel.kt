package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity

interface NamedCallableModel : CallableModel<ParameterModel> {
    val name: NameEntity
}