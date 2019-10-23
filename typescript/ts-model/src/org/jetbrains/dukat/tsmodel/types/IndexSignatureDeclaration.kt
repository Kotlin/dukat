package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration


data class IndexSignatureDeclaration(
        val indexTypes: List<ParameterDeclaration>,
        val returnType: ParameterValueDeclaration
) : MemberDeclaration, ParameterOwnerDeclaration