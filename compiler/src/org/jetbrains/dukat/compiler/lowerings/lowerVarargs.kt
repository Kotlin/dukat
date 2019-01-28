package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

private fun ParameterValueDeclaration.extractVarargType(): ParameterValueDeclaration {
    if (this is TypeDeclaration) {
        if (value == "Array") {
            return params[0]
        } else if (value == "Any") {
            return this
        }
    }

    return this
}

private class LoweringVarags : ParameterValueLowering {
    override fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration {
        return if (declaration.vararg) declaration.copy(type = declaration.type.extractVarargType()) else declaration
    }
}

fun DocumentRootDeclaration.lowerVarargs(): DocumentRootDeclaration {
    return LoweringVarags().lowerDocumentRoot(this)
}