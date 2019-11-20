package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class ClassConstraint(owner: PropertyOwner, prototype: ObjectConstraint = ObjectConstraint(owner)) : PropertyOwnerConstraint(owner) {
    val propertyNames: Set<String>
        get() = staticMembers.keys

    private val staticMembers = LinkedHashMap<String, Constraint>()

    init {
        this["prototype"] = prototype
    }

    override fun set(name: String, data: Constraint) {
        staticMembers[name] = data
    }

    override fun get(name: String): Constraint? {
        return staticMembers[name]
    }

    override fun resolve(): ClassConstraint {
        val resolvedConstraint = ClassConstraint(owner)

        propertyNames.forEach {
            resolvedConstraint[it] = this[it]!!.resolve()
        }

        return resolvedConstraint
    }
}