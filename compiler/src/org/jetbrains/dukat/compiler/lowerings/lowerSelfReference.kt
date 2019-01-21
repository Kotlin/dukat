package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.compiler.asSelfReference

private class LowerSelffReference : ParameterValueLowering() {
    override fun lowerParameterValue(declaration: ParameterValue): ParameterValue {
        declaration.asSelfReference()?.let {
            return TypeDeclaration("Any", emptyList(), meta = it)
        }

        return super.lowerParameterValue(declaration)
    }
}

fun DocumentRoot.lowerSelfReference(): DocumentRoot {
    return LowerSelffReference().lowerDocumentRoot(this)
}