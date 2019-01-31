package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


class CallSignatureDeclaration(
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>
) : MemberDeclaration