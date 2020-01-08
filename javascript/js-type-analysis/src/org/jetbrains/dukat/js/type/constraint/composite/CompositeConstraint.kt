package org.jetbrains.dukat.js.type.constraint.composite

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.CallableConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.StringTypeConstraint
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint
import org.jetbrains.dukat.js.type.constraint.properties.ObjectConstraint
import org.jetbrains.dukat.js.type.constraint.properties.PropertyOwnerConstraint
import org.jetbrains.dukat.js.type.propertyOwner.PropertyOwner

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

    override fun resolve(resolveAsInput: Boolean) : Constraint {
        val properties = if (resolveAsInput) neededProperties else allProperties

        val resolvedConstraints = getFlatConstraints().map { it.resolve(resolveAsInput) }

        val callableConstraints = resolvedConstraints.filterIsInstance<CallableConstraint>()

        return if (properties.isNotEmpty() || callableConstraints.isNotEmpty()) {
            var parameterCount = -1

            val callsCanBeUnified = callableConstraints.all {
                if (parameterCount < 0) {
                    parameterCount = it.parameterCount
                }

                it.parameterCount == parameterCount
            }

            val resultConstraint: PropertyOwnerConstraint = if (callableConstraints.isNotEmpty() && callsCanBeUnified) {
                FunctionConstraint(
                        owner = owner,
                        overloads = callableConstraints.map { callable ->
                            FunctionConstraint.Overload(
                                    callable.returnConstraints,
                                    List(parameterCount) { i -> "`$i`" to NoTypeConstraint }
                            )
                        }
                )
            } else {
                ObjectConstraint(owner).apply {
                    callableConstraints.forEach {
                        callSignatureConstraints.add(it.resolve(resolveAsInput))
                    }
                }
            }

            properties.forEach { (name, value) ->
                resultConstraint[name] = value
            }

            resultConstraint.resolve(resolveAsInput)
        } else {
            when {
                resolvedConstraints.contains(NumberTypeConstraint) -> NumberTypeConstraint
                resolvedConstraints.contains(BigIntTypeConstraint) -> BigIntTypeConstraint
                resolvedConstraints.contains(BooleanTypeConstraint) -> BooleanTypeConstraint
                resolvedConstraints.contains(StringTypeConstraint) -> StringTypeConstraint
                else -> NoTypeConstraint
            }
        }
    }
}