package org.jetbrains.dukat.ast.model

import org.jetbrains.dukat.ast.AstReflectionType

interface Declaration : AstNode {
    val reflectionType
        get() = AstReflectionType.UNKNOWN_DECLARATION
}