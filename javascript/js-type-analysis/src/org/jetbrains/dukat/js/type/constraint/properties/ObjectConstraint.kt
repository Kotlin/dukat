package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.panic.raiseConcern

class ObjectConstraint(
        owner: PropertyOwner,
        val instantiatedClass: PropertyOwnerConstraint? = null
) : PropertyOwnerConstraint(owner) {
    val propertyNames: Set<String>
        get() = properties.keys

    private val properties = LinkedHashMap<String, Constraint>()

    val callSignatureConstraints = mutableListOf<Constraint>()

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
                    null //TODO make sure an unresolved class being instantiated works
                }
            }
        }
    }

    override fun resolve(resolveAsInput: Boolean): ObjectConstraint {
        val resolvedConstraint = if (instantiatedClass == null) {
            ObjectConstraint(owner)
        } else {
            val resolvedClass = instantiatedClass.resolve()

            if (resolvedClass is PropertyOwnerConstraint) {
                ObjectConstraint(owner, resolvedClass)
            } else {
                raiseConcern("Instantiating constraint which cannot be instantiated!") { ObjectConstraint(owner) }
            }
        }

        resolvedConstraint.callSignatureConstraints.addAll(
                callSignatureConstraints.map { it.resolve() }
        )

        propertyNames.forEach {
            resolvedConstraint[it] = this[it]!!.resolve()
        }

        return resolvedConstraint
    }
}