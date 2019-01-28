package org.jetbrains.dukat.ast.model.declaration.types

import org.jetbrains.dukat.ast.model.declaration.Declaration

interface ParameterValueDeclaration : Declaration {
    val vararg: Boolean
    val nullable: Boolean
    var meta: ParameterValueDeclaration?
}