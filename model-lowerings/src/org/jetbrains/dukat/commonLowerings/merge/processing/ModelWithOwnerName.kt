package org.jetbrains.dukat.commonLowerings.merge.processing

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ModuleModel

data class ModelWithOwnerName<T>(val model: T, val ownerName: NameEntity)