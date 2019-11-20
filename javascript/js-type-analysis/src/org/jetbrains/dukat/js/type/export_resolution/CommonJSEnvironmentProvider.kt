package org.jetbrains.dukat.js.type.export_resolution

import org.jetbrains.dukat.js.type.constraint.properties.ObjectConstraint
import org.jetbrains.dukat.js.type.property_owner.Scope

class CommonJSEnvironmentProvider : EnvironmentProvider {
    override fun getEnvironment(): Scope {
        val env = Scope(null)

        val moduleObject = ObjectConstraint(env)
        val exportsObject = ObjectConstraint(env)

        moduleObject["exports"] = exportsObject
        env["module"] = moduleObject
        env["exports"] = exportsObject

        return env
    }
}