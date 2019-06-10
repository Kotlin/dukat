package org.jetbrains.dukat.moduleNameResolver

import kotlinx.serialization.Serializable

@Serializable
internal data class PackageJsonModel(val name: String? = null)