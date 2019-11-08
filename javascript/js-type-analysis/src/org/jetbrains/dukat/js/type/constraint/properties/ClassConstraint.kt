package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class ClassConstraint(val prototype: ObjectConstraint = ObjectConstraint()) : PropertyOwnerConstraint {
    override val propertyNames: Set<String>
        get() = staticMembers.keys

    private val staticMembers = LinkedHashMap<String, Constraint>()

    override fun set(name: String, data: Constraint) {
        staticMembers[name] = data
    }

    override fun has(name: String): Boolean {
        return staticMembers.containsKey(name)
    }

    override fun get(name: String): Constraint? {
        return staticMembers[name]
    }

    override fun resolve(owner: PropertyOwner): Constraint {
        val resolvedConstraint = ClassConstraint(prototype.resolve(owner) as ObjectConstraint)

        propertyNames.forEach {
            resolvedConstraint[it] = this[it]!!.resolve(owner)
        }

        return resolvedConstraint
    }
}