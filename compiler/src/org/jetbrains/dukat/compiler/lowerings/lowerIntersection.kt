package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.compiler.asIntersection

private class LowerIntersection : ParameterValueLowering() {
    override fun lowerParameterValue(declaration: ParameterValue): ParameterValue {
        declaration.asIntersection()?.let {
            val firstIntersectionType = it.params[0]
            firstIntersectionType.meta = it.copy()
            return lowerParameterValue(firstIntersectionType)
        }

        return super.lowerParameterValue(declaration)
    }
}

fun DocumentRoot.lowerIntersectionType(): DocumentRoot {
    return LowerIntersection().lowerDocumentRoot(this)
}