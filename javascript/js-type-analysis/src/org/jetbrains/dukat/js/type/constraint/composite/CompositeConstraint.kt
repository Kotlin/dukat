package org.jetbrains.dukat.js.type.constraint.composite

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.RecursiveConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.StringTypeConstraint
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint
import org.jetbrains.dukat.js.type.constraint.properties.ObjectConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class CompositeConstraint(
        override val owner: PropertyOwner,
        private val constraints: MutableSet<Constraint>
) : PropertyOwner, Constraint {
    constructor(owner: PropertyOwner, vararg constraints: Constraint) : this(owner, mutableSetOf(*constraints))
    constructor(owner: PropertyOwner, constraints: List<Constraint>) : this(owner, constraints.toMutableSet())

    private val neededProperties = LinkedHashMap<String, Constraint>()
    private val allProperties = LinkedHashMap<String, Constraint>()

    override fun set(name: String, data: Constraint) {
        allProperties[name] = data
    }

    override fun get(name: String) : Constraint? {
        val constraint = allProperties[name]

        return if (constraint != null) {
            constraint
        } else {
            val newConstraint = CompositeConstraint(owner)
            neededProperties[name] = newConstraint
            allProperties[name] = newConstraint
            newConstraint
        }
    }

    override operator fun plusAssign(other: Constraint) {
        constraints += other
    }

    override operator fun plusAssign(others: Collection<Constraint>) {
        constraints.addAll(others)
    }

    private fun getFlatConstraints() : List<Constraint> {
        return constraints.flatMap { constraint ->
            if(constraint is CompositeConstraint) {
                constraint.getFlatConstraints()
            } else {
                listOf(constraint)
            }
        }
    }

    private fun resolveToBasicType() : Constraint {
        val resolvedConstraints = getFlatConstraints().map { it.resolve() }

        return when {
            resolvedConstraints.contains(NumberTypeConstraint) -> NumberTypeConstraint
            resolvedConstraints.contains(BigIntTypeConstraint) -> BigIntTypeConstraint
            resolvedConstraints.contains(BooleanTypeConstraint) -> BooleanTypeConstraint
            resolvedConstraints.contains(StringTypeConstraint) -> StringTypeConstraint
            resolvedConstraints.contains(RecursiveConstraint) -> RecursiveConstraint
            else -> NoTypeConstraint
        }
    }

    private fun resolveWithProperties(properties: Map<String, Constraint>, resolveConstraint: (Constraint) -> Constraint) : Constraint {
        return if (properties.isNotEmpty()) {
            ObjectConstraint(owner).apply {
                properties.forEach { (name, value) ->
                    this[name] = resolveConstraint(value)
                }
            }
        } else {
            resolveToBasicType()
        }
    }

    override fun resolve() = resolveWithProperties(allProperties, Constraint::resolve)

    override fun resolveAsInput() = resolveWithProperties(neededProperties, Constraint::resolveAsInput)
}