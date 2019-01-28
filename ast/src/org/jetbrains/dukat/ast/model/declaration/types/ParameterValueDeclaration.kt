package org.jetbrains.dukat.ast.model.declaration.types

import org.jetbrains.dukat.ast.model.declaration.Declaration

interface ParameterValueDeclaration : Declaration {
    val nullable: Boolean
    var meta: ParameterValueDeclaration?
}