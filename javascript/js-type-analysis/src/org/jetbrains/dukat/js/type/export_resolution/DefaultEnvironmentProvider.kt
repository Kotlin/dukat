package org.jetbrains.dukat.js.type.export_resolution

import org.jetbrains.dukat.js.type.property_owner.Scope

class DefaultEnvironmentProvider : EnvironmentProvider {
    override fun getEnvironment(): Scope {
        return Scope(null)
    }
}