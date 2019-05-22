package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.IdentifierEntity

data class GenericTypeDeclaration(
    val name: IdentifierEntity,
    val scope: GenericTypeScope
) : ParameterValueDeclaration {
    override val nullable: Boolean
        get() = throw UnsupportedOperationException("never supposed to be called")
    override var meta: ParameterValueDeclaration?
        get() = throw UnsupportedOperationException("never supposed to be called")
        set(value) {}
}