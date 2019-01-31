package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.compiler.asSelfReference
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

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