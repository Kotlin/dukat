package org.jetbrains.dukat.js.type.property_owner

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.reference.ReferenceConstraint

class Scope : PropertyOwner {
    val propertyNames: Set<String>
        get() = properties.keys

    private val properties = LinkedHashMap<String, Constraint>()

    override fun set(name: String, data: Constraint) {
        properties[name] = data
    }

    override fun get(name: String): Constraint {
        return properties[name] ?: ReferenceConstraint(IdentifierEntity(name))
    }
}