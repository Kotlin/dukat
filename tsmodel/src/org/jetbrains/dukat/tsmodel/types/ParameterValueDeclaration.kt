package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.AstTypeEntity

interface ParameterValueDeclaration : AstTypeEntity {
    val nullable: Boolean
    var meta: ParameterValueDeclaration?
}