package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration

fun ParameterValue.asIntersection(): TypeDeclaration? {
    if ((this is TypeDeclaration) && (value == "@@Intersection")) {
        return this
    }

    return null
}
