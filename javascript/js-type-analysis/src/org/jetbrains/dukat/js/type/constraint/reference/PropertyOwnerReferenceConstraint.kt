package org.jetbrains.dukat.js.type.constraint.reference

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.properties.PropertyOwnerConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.panic.raiseConcern

abstract class PropertyOwnerReferenceConstraint : PropertyOwnerConstraint {
    override val propertyNames: Set<String>
        get() = raiseConcern("Properties of a reference should never be listed.") { emptySet() }

    protected val modifiedProperties = LinkedHashMap<String, Constraint>()

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
    protected fun Constraint.resolveWithProperties(owner: PropertyOwner) : Constraint {
        if(this is PropertyOwner) {
            modifiedProperties.forEach { (name, constraint) ->
                //TODO take composite constraints into account here
                this[name] = constraint
            }
        }

        return this.resolve(owner)
    }
}