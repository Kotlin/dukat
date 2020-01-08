package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.propertyOwner.PropertyOwner

class ClassConstraint(owner: PropertyOwner, prototype: ObjectConstraint = ObjectConstraint(owner)) : PropertyOwnerConstraint(owner) {
    val propertyNames: Set<String>
        get() = staticMembers.keys

    var constructorConstraint: Constraint? = null

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

    override fun resolve(resolveAsInput: Boolean): ClassConstraint {
        val constructorConstraint = constructorConstraint
        if (constructorConstraint != null) {
            this.constructorConstraint = constructorConstraint.resolve()
        }

        propertyNames.forEach {
            this[it] = this[it]!!.resolve()
        }

        return this
    }
}