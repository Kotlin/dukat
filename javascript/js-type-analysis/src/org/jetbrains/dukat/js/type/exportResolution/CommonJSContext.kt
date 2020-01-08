package org.jetbrains.dukat.js.type.exportResolution

import org.jetbrains.dukat.js.type.constraint.properties.ObjectConstraint
import org.jetbrains.dukat.js.type.constraint.resolution.asDefaultToDeclarations
import org.jetbrains.dukat.js.type.propertyOwner.Scope
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

    override fun getExportsFrom(environment: Scope, defaultExportName: String): List<TopLevelDeclaration> {
        val moduleObject = environment["module"]

        if (moduleObject is ObjectConstraint) {
            val exportsObject = moduleObject["exports"]

            if (exportsObject != null) {
                return if (exportsObject is ObjectConstraint && exportsObject.callSignatureConstraints.isEmpty()) {
                    basicContext.getExportsFrom(
                            Scope(null).apply {
                                exportsObject.propertyNames.forEach {
                                    this[it] = exportsObject[it]!!
                                }
                            },
                            defaultExportName
                    )
                } else {
                    exportsObject.asDefaultToDeclarations(defaultExportName)
                }
            }
        }

        return raiseConcern("No exports found!") { emptyList() }
    }
}