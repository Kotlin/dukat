package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.ast.AstContext
import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeAliasDeclaration
import org.jetbrains.dukat.compiler.lowerPrimitives
import org.jetbrains.dukat.compiler.lowerings.eliminateStringType
import org.jetbrains.dukat.compiler.lowerings.escapeIdentificators
import org.jetbrains.dukat.compiler.lowerings.introduceMemberNodes
import org.jetbrains.dukat.compiler.lowerings.lowerConstructors
import org.jetbrains.dukat.compiler.lowerings.lowerInheritance
import org.jetbrains.dukat.compiler.lowerings.lowerIntersectionType
import org.jetbrains.dukat.compiler.lowerings.lowerNativeArray
import org.jetbrains.dukat.compiler.lowerings.lowerNullable
import org.jetbrains.dukat.compiler.lowerings.lowerObjectLiterals
import org.jetbrains.dukat.compiler.lowerings.lowerOverrides
import org.jetbrains.dukat.compiler.lowerings.lowerSelfReference
import org.jetbrains.dukat.compiler.lowerings.lowerTypeAliases
import org.jetbrains.dukat.compiler.lowerings.lowerVarargs

private fun DocumentRootDeclaration.updateContext(astContext: AstContext): DocumentRootDeclaration {
    for (declaration in declarations) {
        if (declaration is InterfaceDeclaration) {
            astContext.registerInterface(declaration)
        }
        if (declaration is ClassDeclaration) {
            astContext.registerClass(declaration)
        }
        if (declaration is DocumentRootDeclaration) {
            declaration.updateContext(astContext)
        }

        if (declaration is TypeAliasDeclaration) {
            astContext.registerTypeAlias(declaration)
        }
    }

    return this
}

interface InputTranslator {
    fun translateFile(fileName: String): DocumentRootDeclaration
    fun release()

    fun lower(documentRoot: DocumentRootDeclaration): DocumentRootDeclaration {
        val myAstContext = AstContext()

        return documentRoot
                .introduceMemberNodes()
                .eliminateStringType()
                .lowerObjectLiterals()
                .lowerConstructors()
                .lowerNativeArray()
                .lowerNullable()
                .lowerPrimitives()
                .escapeIdentificators()
                .lowerVarargs()
                .lowerIntersectionType()
                .lowerSelfReference()
                .updateContext(myAstContext)
                .lowerInheritance(myAstContext)
                .lowerTypeAliases(myAstContext)
                .lowerOverrides()

    }
}
