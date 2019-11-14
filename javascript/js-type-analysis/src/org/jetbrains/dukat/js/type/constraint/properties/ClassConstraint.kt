package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class ClassConstraint(val prototype: ObjectConstraint = ObjectConstraint()) : PropertyOwnerConstraint {
    val propertyNames: Set<String>
        get() = staticMembers.keys

    private val staticMembers = LinkedHashMap<String, Constraint>()

    override fun set(name: String, data: Constraint) {
        staticMembers[name] = data
    }

    override fun get(name: String): Constraint? {
        return staticMembers[name]
    }

    override fun resolve(owner: PropertyOwner): ClassConstraint {
        val resolvedConstraint = ClassConstraint(prototype = prototype.resolve(owner))

        propertyNames.forEach {
            resolvedConstraint[it] = this[it]!!.resolve(owner)
        }

        return resolvedConstraint
    }
}