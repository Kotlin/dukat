package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.RecursiveConstraint
import org.jetbrains.dukat.js.type.constraint.resolution.ResolutionState
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

class FunctionConstraint(
        owner: PropertyOwner,
        val returnConstraints: Constraint,
        val parameterConstraints: List<Pair<String, Constraint>>
) : PropertyOwnerConstraint(owner) {
    private val classRepresentation = ClassConstraint(owner)

    override fun set(name: String, data: Constraint) {
        classRepresentation[name] = data
    }

    override fun get(name: String): Constraint? {
        return classRepresentation[name]
    }


    private var resolutionState = ResolutionState.UNRESOLVED
    private var resolvedConstraint: Constraint? = null

    private fun isPrototypeFunction() : Boolean {
        val onlyHasPrototype = classRepresentation.propertyNames.size == 1 && classRepresentation.propertyNames.contains("prototype")

        return if (onlyHasPrototype) {
            val resolvedPrototype = classRepresentation["prototype"]!!.resolve()

            if (resolvedPrototype is ObjectConstraint) {
                resolvedPrototype.propertyNames.isNotEmpty()
            } else {
                false
            }
        } else {
            true
        }
    }

    private fun doResolve(): Constraint {
        return if (!isPrototypeFunction()) {
            FunctionConstraint(
                    owner,
                    returnConstraints.resolve(),
                    parameterConstraints.map { (name, constraint) -> name to constraint.resolve() }
            )
        } else {
            //TODO add function as constructor, and invocable
            return classRepresentation.resolve()
        }
    }

    override fun resolve(): Constraint {
        return when (resolutionState) {
            ResolutionState.UNRESOLVED -> {
                resolutionState = ResolutionState.RESOLVING
                resolvedConstraint = doResolve()
                resolutionState = ResolutionState.RESOLVED

                resolvedConstraint!!
            }

            ResolutionState.RESOLVING -> {
                FunctionConstraint(
                        owner,
                        RecursiveConstraint,
                        parameterConstraints.map { (name, _) -> name to NoTypeConstraint }
                )
            }

            ResolutionState.RESOLVED -> {
                resolvedConstraint!!
            }
        }
    }
}