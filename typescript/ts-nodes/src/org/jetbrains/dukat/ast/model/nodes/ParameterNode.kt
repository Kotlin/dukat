package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

data class ParameterNode(
        val name: String,
        val type: ParameterValueDeclaration,
        val initializer: TypeDeclaration?,

        val vararg: Boolean,
        val optional: Boolean
) : Entity