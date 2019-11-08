package org.jetbrains.dukat.js.type.property_owner

import org.jetbrains.dukat.js.type.constraint.Constraint

class Scope : PropertyOwner {
    override val propertyNames: Set<String>
        get() = properties.keys

    private val properties = LinkedHashMap<String, Constraint>()

    override fun set(name: String, data: Constraint) {
        properties[name] = data
    }

    override fun has(name: String): Boolean {
        return properties.containsKey(name)
    }

    override fun get(name: String): Constraint? {
        return properties[name]
    }
}