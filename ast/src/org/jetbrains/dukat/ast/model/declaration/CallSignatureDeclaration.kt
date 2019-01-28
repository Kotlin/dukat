package org.jetbrains.dukat.ast.model.declaration

import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration

class CallSignatureDeclaration(
    val parameters: List<ParameterDeclaration>,
    val type: ParameterValueDeclaration,
    val typeParameters: List<TypeParameterDeclaration>
) : MemberDeclaration