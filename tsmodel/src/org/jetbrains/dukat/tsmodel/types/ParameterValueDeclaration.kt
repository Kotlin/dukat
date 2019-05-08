package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.AstEntity


interface
ParameterValueDeclaration : AstEntity {
    val nullable: Boolean
    var meta: ParameterValueDeclaration?
}