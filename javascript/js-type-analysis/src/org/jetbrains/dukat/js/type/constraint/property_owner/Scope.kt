package org.jetbrains.dukat.js.type.constraint.property_owner

import org.jetbrains.dukat.js.type.constraint.container.ConstraintContainer

class Scope : PropertyOwner {
    override val propertyNames: Set<String>
        get() = properties.keys

    private val properties = LinkedHashMap<String, ConstraintContainer>()

    override fun set(name: String, data: ConstraintContainer) {
        properties[name] = data
    }

    override fun has(name: String): Boolean {
        return properties.containsKey(name)
    }

    override fun get(name: String): ConstraintContainer? {
        return properties[name]
    }
}