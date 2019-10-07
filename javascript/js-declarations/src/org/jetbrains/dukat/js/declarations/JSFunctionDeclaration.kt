package org.jetbrains.dukat.js.declarations

import org.jetbrains.dukat.astCommon.IdentifierEntity

data class JSFunctionDeclaration(
        val name: IdentifierEntity,
        val parameters: List<JSParameterDeclaration>
)