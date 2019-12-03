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

    private fun doResolve(): Constraint {
        return if (!hasMembers()) {
            FunctionConstraint(
                    owner,
                    returnConstraints.resolve(),
                    parameterConstraints.map { (name, constraint) -> name to constraint.resolveAsInput() }
            )
        } else {
            if (hasNonStaticMembers()) {
                classRepresentation.constructorConstraint = FunctionConstraint(
                        owner,
                        returnConstraints.resolve(),
                        parameterConstraints.map { (name, constraint) -> name to constraint.resolveAsInput() }
                )

                classRepresentation.resolve()
            } else {
                val objectRepresentation = ObjectConstraint(owner)

                val propertyNames = classRepresentation.propertyNames.filter { it != "prototype" }

                propertyNames.forEach {
                    objectRepresentation[it] = classRepresentation[it]!!
                }

                objectRepresentation.callSignatureConstraint = FunctionConstraint(
                        owner,
                        returnConstraints.resolve(),
                        parameterConstraints.map { (name, constraint) -> name to constraint.resolveAsInput() }
                )

                objectRepresentation.resolve()
            }
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