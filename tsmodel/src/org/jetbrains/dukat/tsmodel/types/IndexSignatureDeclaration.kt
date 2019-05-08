package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.AstMemberEntity
import org.jetbrains.dukat.tsmodel.ParameterDeclaration


data class IndexSignatureDeclaration(
        val indexTypes: List<ParameterDeclaration>,
        val returnType: ParameterValueDeclaration
) : AstMemberEntity