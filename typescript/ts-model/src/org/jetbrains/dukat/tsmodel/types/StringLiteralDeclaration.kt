package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.MetaData

data class StringLiteralDeclaration(
        val token: String,

        override val nullable: Boolean = false,
        override var meta: MetaData? = null
): ParameterValueDeclaration