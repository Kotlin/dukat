package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.ast.AstContext
import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.compiler.lowerPrimitives
import org.jetbrains.dukat.compiler.lowerings.lowerConstructors
import org.jetbrains.dukat.compiler.lowerings.lowerInheritance
import org.jetbrains.dukat.compiler.lowerings.lowerIntersectionType
import org.jetbrains.dukat.compiler.lowerings.lowerNativeArray
import org.jetbrains.dukat.compiler.lowerings.lowerNullable
import org.jetbrains.dukat.compiler.lowerings.lowerObjectLiterals
import org.jetbrains.dukat.compiler.lowerings.lowerOverrides
import org.jetbrains.dukat.compiler.lowerings.lowerSelfReference
import org.jetbrains.dukat.compiler.lowerings.lowerVarargs

private fun DocumentRoot.updateContext(astContext: AstContext): DocumentRoot {
    for (declaration in declarations) {
        if (declaration is InterfaceDeclaration) {
            astContext.registerInterface(declaration)
        }
        if (declaration is ClassDeclaration) {
            astContext.registerClass(declaration)
        }
        if (declaration is DocumentRoot) {
            declaration.updateContext(astContext)
        }
    }

    return this
}

interface InputTranslator {
    fun translateFile(fileName: String): DocumentRoot
    fun release()

    fun lower(documentRoot: DocumentRoot): DocumentRoot {
        val myAstContext = AstContext()

        return documentRoot
                .lowerObjectLiterals()
                .lowerConstructors()
                .lowerNativeArray()
                .lowerNullable()
                .lowerPrimitives()
                .lowerVarargs()
                .lowerIntersectionType()
                .lowerSelfReference()
                .updateContext(myAstContext)
                .lowerInheritance(myAstContext)
                .lowerOverrides()

    }
}
