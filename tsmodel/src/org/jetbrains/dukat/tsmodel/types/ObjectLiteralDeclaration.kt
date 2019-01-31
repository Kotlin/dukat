package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.MemberDeclaration

data class ObjectLiteralDeclaration(
        val members: List<MemberDeclaration>,
        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration, MemberDeclaration