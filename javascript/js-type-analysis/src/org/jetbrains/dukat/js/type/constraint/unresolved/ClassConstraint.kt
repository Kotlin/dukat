package org.jetbrains.dukat.js.type.constraint.unresolved

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.container.ConstraintContainer
import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner

class ClassConstraint(val prototype: ObjectConstraint = ObjectConstraint()) : PropertyOwner, Constraint {
    override val propertyNames: Set<String>
        get() = staticMembers.keys

    private val staticMembers = LinkedHashMap<String, ConstraintContainer>()

    override fun set(name: String, data: ConstraintContainer) {
        staticMembers[name] = data
    }

    override fun has(name: String): Boolean {
        return staticMembers.containsKey(name)
    }

    override fun get(name: String): ConstraintContainer? {
        return staticMembers[name]
    }

    override fun resolve(owner: PropertyOwner): Constraint {
        val resolvedConstraint = ClassConstraint(prototype.resolve(owner) as ObjectConstraint)

        propertyNames.forEach {
            resolvedConstraint[it] = ConstraintContainer(this[it]!!.resolve(owner))
        }

        return resolvedConstraint
    }
}