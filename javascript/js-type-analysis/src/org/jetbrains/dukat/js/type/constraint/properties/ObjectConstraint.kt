package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.panic.raiseConcern

class ObjectConstraint(
        val instantiatedClass: PropertyOwnerConstraint? = null
) : PropertyOwnerConstraint {
    val propertyNames: Set<String>
        get() = properties.keys

    private val properties = LinkedHashMap<String, Constraint>()

    override fun set(name: String, data: Constraint) {
        properties[name] = data
    }

    override fun get(name: String): Constraint? {
        return when {
            properties.containsKey(name) -> properties[name]
            else -> {
                if(instantiatedClass != null) {
                    val classPrototype = instantiatedClass["prototype"]

                    if (classPrototype is PropertyOwner) {
                        classPrototype[name]
                    } else {
                        raiseConcern("Instantiating constraint which cannot be instantiated!") { null }
                    }
                } else {
                    null
                }
            }
        }
    }

    override fun resolve(owner: PropertyOwner): ObjectConstraint {
        val resolvedConstraint = if (instantiatedClass == null) {
            ObjectConstraint()
        } else {
            val resolvedClass = instantiatedClass.resolve(owner)

            if (resolvedClass is PropertyOwnerConstraint) {
                ObjectConstraint(resolvedClass)
            } else {
                raiseConcern("Instantiating constraint which cannot be instantiated!") { ObjectConstraint() }
            }
        }

        propertyNames.forEach {
            resolvedConstraint[it] = this[it]!!.resolve(owner)
        }

        return resolvedConstraint
    }
}