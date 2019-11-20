package org.jetbrains.dukat.js.type.export_resolution

import org.jetbrains.dukat.js.type.property_owner.Scope

class EmptyEnvironmentProvider : EnvironmentProvider {
    override fun getEnvironment(): Scope {
        return Scope(null)
    }
}