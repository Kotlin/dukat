package org.jetbrains.dukat.js.type.export_resolution

import org.jetbrains.dukat.js.type.property_owner.Scope

interface EnvironmentProvider {
    fun getEnvironment(): Scope
}