package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration
import org.jetbrains.dukat.compiler.asSelfReference

private class LowerSelffReference : ParameterValueLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        declaration.asSelfReference()?.let {
            return TypeDeclaration("Any", emptyList(), meta = it)
        }

        return super.lowerParameterValue(declaration)
    }
}

fun DocumentRootDeclaration.lowerSelfReference(): DocumentRootDeclaration {
    return LowerSelffReference().lowerDocumentRoot(this)
}