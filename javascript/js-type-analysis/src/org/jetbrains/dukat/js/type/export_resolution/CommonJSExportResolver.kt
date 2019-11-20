package org.jetbrains.dukat.js.type.export_resolution

import org.jetbrains.dukat.js.type.constraint.properties.ObjectConstraint
import org.jetbrains.dukat.js.type.property_owner.Scope
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

class CommonJSExportResolver : ExportResolver {
    private val generalExportResolver = GeneralExportResolver()

    override fun resolve(scope: Scope): List<TopLevelDeclaration> {
        val moduleObject = scope["module"]

        if (moduleObject is ObjectConstraint) {
            val exportsObject = moduleObject["exports"]

            if (exportsObject != null) {
                return generalExportResolver.resolve(
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