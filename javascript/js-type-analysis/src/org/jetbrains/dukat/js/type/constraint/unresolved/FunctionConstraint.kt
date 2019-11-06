package org.jetbrains.dukat.js.type.constraint.unresolved

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.container.ConstraintContainer
import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner

class FunctionConstraint(
        val returnConstraints: ConstraintContainer,
        val parameterConstraints: List<Pair<String, ConstraintContainer>>
) : PropertyOwner, Constraint {
    override val propertyNames: Set<String>
        get() = properties.keys

    private val properties = LinkedHashMap<String, ConstraintContainer>()

    override fun set(name: String, data: ConstraintContainer) {
        properties[name] = data
    }

    override fun has(name: String): Boolean {
        return properties.containsKey(name)
    }

    override fun get(name: String): ConstraintContainer? {
        return properties[name]
    }

    override fun resolve(owner: PropertyOwner): Constraint {
        return FunctionConstraint(
                ConstraintContainer(returnConstraints.resolve(owner)),
                parameterConstraints.map { (name, constraint) -> name to ConstraintContainer(constraint.resolve(owner)) }
        )
    }
}