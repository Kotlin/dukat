package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.RecursiveConstraint
import org.jetbrains.dukat.js.type.constraint.resolution.ResolutionState
import org.jetbrains.dukat.js.type.propertyOwner.PropertyOwner

class FunctionConstraint(
        owner: PropertyOwner,
        val overloads: List<Overload>
) : PropertyOwnerConstraint(owner) {

    data class Overload(
        val returnConstraints: Constraint,
        val parameterConstraints: List<Pair<String, Constraint>>
    )

    private val classRepresentation = ClassConstraint(owner)

    override fun set(name: String, data: Constraint) {
        classRepresentation[name] = data
    }

    override fun get(name: String): Constraint? {
        return classRepresentation[name]
    }


    private var resolutionState = ResolutionState.UNRESOLVED
    private var resolvedConstraint: Constraint? = null

    private fun getParameterNames() : List<String> {
        val parameters = mutableListOf<String>()

        overloads.forEach {
            it.parameterConstraints.forEachIndexed { index, (name, _) ->
                if (parameters.size <= index) {
                    parameters.add(index, name)
                }
            }
        }

        return parameters
    }

    private fun hasMembers() : Boolean {
        return if (classRepresentation.propertyNames.isNotEmpty()) {
            if (classRepresentation.propertyNames == setOf("prototype")) {
                val resolvedPrototype = classRepresentation["prototype"]!!.resolve()

                if (resolvedPrototype is ObjectConstraint) {
                    resolvedPrototype.propertyNames.isNotEmpty()
                } else {
                    false
                }
            } else {
                true
            }
        } else {
            false
        }
    }

    private fun hasNonStaticMembers() : Boolean {
        val prototype = classRepresentation["prototype"]

        return if (prototype != null) {
            val resolvedPrototype = prototype.resolve()

            if (resolvedPrototype is ObjectConstraint) {
                resolvedPrototype.propertyNames.isNotEmpty()
            } else {
                false
            }
        } else {
            false
        }
    }

    private fun getResolvedCallable() = FunctionConstraint(
            owner,
            overloads.map {
                Overload(
                        it.returnConstraints.resolve(),
                        it.parameterConstraints.map { (name, constraint) -> name to constraint.resolve(resolveAsInput = true) }
                )
            }
    )

    private fun doResolve(): Constraint {
        return if (!hasMembers()) {
            getResolvedCallable()
        } else {
            if (hasNonStaticMembers()) {
                classRepresentation.constructorConstraint = getResolvedCallable()
                classRepresentation.resolve()
            } else {
                val objectRepresentation = ObjectConstraint(owner)

                val propertyNames = classRepresentation.propertyNames.filter { it != "prototype" }

                propertyNames.forEach {
                    objectRepresentation[it] = classRepresentation[it]!!
                }

                objectRepresentation.callSignatureConstraints.add(getResolvedCallable())

                objectRepresentation.resolve()
            }
        }
    }

    override fun resolve(resolveAsInput: Boolean): Constraint {
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
                        listOf(Overload(
                                returnConstraints = RecursiveConstraint,
                                parameterConstraints = getParameterNames().map { it to NoTypeConstraint }
                        ))
                )
            }

            ResolutionState.RESOLVED -> {
                resolvedConstraint!!
            }
        }
    }
}