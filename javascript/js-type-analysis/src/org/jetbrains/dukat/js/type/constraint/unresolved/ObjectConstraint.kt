package org.jetbrains.dukat.js.type.constraint.unresolved

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.container.ConstraintContainer
import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner

class ObjectConstraint(
        private val instantiatedClass: ClassConstraint? = null
) : PropertyOwner, Constraint {
    override val propertyNames: Set<String>
        get() {
            val names = mutableSetOf<String>()
            names.addAll(properties.keys)
            instantiatedClass?.let {
                names.addAll(it.prototype.propertyNames)
            }
            return names
        }

    private val properties = LinkedHashMap<String, ConstraintContainer>()

    override fun set(name: String, data: ConstraintContainer) {
        properties[name] = data
    }

    override fun has(name: String): Boolean {
        return properties.containsKey(name) || instantiatedClass?.prototype?.has(name) == true
    }

    override fun get(name: String): ConstraintContainer? {
        return when {
            properties.containsKey(name) -> properties[name]
            instantiatedClass?.prototype?.has(name) == true -> instantiatedClass.prototype[name]
            else -> null
        }
    }

    override fun resolve(owner: PropertyOwner): Constraint {
        val resolvedConstraint = ObjectConstraint()

        propertyNames.forEach {
            resolvedConstraint[it] = ConstraintContainer(this[it]!!.resolve(owner))
        }

        return resolvedConstraint
    }
}