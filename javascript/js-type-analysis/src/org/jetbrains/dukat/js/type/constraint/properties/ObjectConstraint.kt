package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration

class ObjectConstraint(
        private val instantiatedClass: ClassConstraint? = null
) : PropertyOwnerConstraint {
    val propertyNames: Set<String>
        get() {
            val names = mutableSetOf<String>()
            names.addAll(properties.keys)
            instantiatedClass?.let {
                names.addAll(it.prototype.propertyNames)
            }
            return names
        }

    private val properties = LinkedHashMap<String, Constraint>()

    override fun set(name: String, data: Constraint) {
        properties[name] = data
    }

    override fun get(name: String): Constraint? {
        return when {
            properties.containsKey(name) -> properties[name]
            else -> instantiatedClass?.prototype?.get(name)
        }
    }

    override fun resolve(owner: PropertyOwner): ObjectConstraint {
        val resolvedConstraint = ObjectConstraint()

        propertyNames.forEach {
            resolvedConstraint[it] = this[it]!!.resolve(owner)
        }

        return resolvedConstraint
    }
}