package org.jetbrains.dukat.ast.model.declaration.types

import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration

data class IndexSignatureDeclaration(
        val indexTypes: List<ParameterDeclaration>,
        val returnType: ParameterValueDeclaration
) : MemberDeclaration