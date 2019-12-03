package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class ImportModel(val name: NameEntity, val asAlias: String? = null) : Entity