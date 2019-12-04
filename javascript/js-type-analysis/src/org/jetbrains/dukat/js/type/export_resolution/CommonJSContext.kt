package org.jetbrains.dukat.js.type.export_resolution

import org.jetbrains.dukat.js.type.constraint.properties.ObjectConstraint
import org.jetbrains.dukat.js.type.property_owner.Scope
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

class CommonJSContext : TypeAnalysisContext {
    private val basicContext = BasicJSContext()

    override fun getEnvironment(): Scope {
        val env = basicContext.getEnvironment()

        val moduleObject = ObjectConstraint(env)
        val exportsObject = ObjectConstraint(env)

        moduleObject["exports"] = exportsObject
        env["module"] = moduleObject
        env["exports"] = exportsObject

        return env
    }

    override fun getExportsFrom(environment: Scope): List<TopLevelDeclaration> {
        val moduleObject = environment["module"]

        if (moduleObject is ObjectConstraint) {
            val exportsObject = moduleObject["exports"]

            if (exportsObject != null) {
                return basicContext.getExportsFrom(
                        Scope(null).apply {
                            if (exportsObject is ObjectConstraint) {
                                exportsObject.propertyNames.forEach {
                                    this[it] = exportsObject[it]!!
                                }
                            } else {
                                //TODO figure out what to do in this case
                                this["default"] = exportsObject
                            }
                        }
                )
            }
        }

        return raiseConcern("No exports found!") { emptyList<TopLevelDeclaration>() }
    }
}