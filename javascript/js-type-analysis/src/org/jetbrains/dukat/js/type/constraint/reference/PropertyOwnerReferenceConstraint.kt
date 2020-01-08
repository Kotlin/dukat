package org.jetbrains.dukat.js.type.constraint.reference

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.properties.PropertyOwnerConstraint
import org.jetbrains.dukat.js.type.propertyOwner.PropertyOwner

abstract class PropertyOwnerReferenceConstraint(parent: PropertyOwner) : PropertyOwnerConstraint(parent) {
    private val modifiedProperties = LinkedHashMap<String, Constraint>()

    override fun set(name: String, data: Constraint) {
        modifiedProperties[name] = data
    }

    override fun get(name: String): Constraint {
        return modifiedProperties[name] ?: ReferenceConstraint(IdentifierEntity(name), this)
    }

    /**
     * Called at resolving stage,
     * to apply properties to the resolved reference.
     */
    protected fun Constraint.resolveWithProperties() : Constraint {
        val copy = this.resolve()

        if(copy is PropertyOwner) {
            modifiedProperties.forEach { (name, constraint) ->
                copy[name] = constraint
            }
        }

        return copy.resolve()
    }
}