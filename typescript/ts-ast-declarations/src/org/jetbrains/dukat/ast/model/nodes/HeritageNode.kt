package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


data class HeritageNode(
        val name: NameEntity,
        val typeArguments: List<ParameterValueDeclaration>
) : Entity
