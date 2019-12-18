package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

data class ExpressionDeclaration(
        val meta: String?
) : Entity