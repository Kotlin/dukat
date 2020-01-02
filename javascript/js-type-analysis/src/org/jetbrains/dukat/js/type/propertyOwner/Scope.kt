package org.jetbrains.dukat.js.type.propertyOwner

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.reference.ReferenceConstraint

class Scope(override val owner: PropertyOwner?) : PropertyOwner {
    val propertyNames: Set<String>
        get() = properties.keys

    private val properties = LinkedHashMap<String, Constraint>()
    private val references = LinkedHashMap<String, Constraint>()

    override fun set(name: String, data: Constraint) {
        properties[name] = data
    }

    override fun get(name: String): Constraint {
        return properties[name] ?: references.getOrPut(name) { ReferenceConstraint(IdentifierEntity(name), this) }
    }
}