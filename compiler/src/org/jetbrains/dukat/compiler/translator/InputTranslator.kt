package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.compiler.AstContext
import org.jetbrains.dukat.compiler.lowerPrimitives
import org.jetbrains.dukat.compiler.lowerings.eliminateStringType
import org.jetbrains.dukat.compiler.lowerings.escapeIdentificators
import org.jetbrains.dukat.compiler.lowerings.filterOutNonDeclarations
import org.jetbrains.dukat.compiler.lowerings.generateInterfaceReferences
import org.jetbrains.dukat.compiler.lowerings.introduceExports
import org.jetbrains.dukat.compiler.lowerings.introduceGeneratedEntities
import org.jetbrains.dukat.compiler.lowerings.introduceModuleMetadata
import org.jetbrains.dukat.compiler.lowerings.introduceNodes
import org.jetbrains.dukat.compiler.lowerings.lowerConstructors
import org.jetbrains.dukat.compiler.lowerings.lowerIntersectionType
import org.jetbrains.dukat.compiler.lowerings.lowerNativeArray
import org.jetbrains.dukat.compiler.lowerings.lowerNullable
import org.jetbrains.dukat.compiler.lowerings.lowerOverrides
import org.jetbrains.dukat.compiler.lowerings.lowerSelfReference
import org.jetbrains.dukat.compiler.lowerings.lowerTypeAliases
import org.jetbrains.dukat.compiler.lowerings.lowerUnionType
import org.jetbrains.dukat.compiler.lowerings.lowerVarargs
import org.jetbrains.dukat.compiler.lowerings.specifyDynamicTypes
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration

private fun DocumentRootNode.updateContext(astContext: AstContext): DocumentRootNode {
    for (declaration in declarations) {
        if (declaration is InterfaceNode) {
            astContext.registerInterface(declaration)
        }
        if (declaration is ClassNode) {
            astContext.registerClass(declaration)
        }
        if (declaration is DocumentRootNode) {
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

    fun lower(documentRoot: DocumentRootDeclaration): DocumentRootNode {
        val myAstContext = AstContext()

        return documentRoot
                .filterOutNonDeclarations()
                .introduceNodes()
                .introduceModuleMetadata()
                .introduceExports()
                .generateInterfaceReferences(myAstContext)
                .introduceGeneratedEntities(myAstContext)
                .eliminateStringType()
                .lowerNativeArray()
                .lowerNullable()
                .lowerPrimitives()
                .escapeIdentificators()
                .lowerUnionType()
                .lowerVarargs()
                .lowerIntersectionType()
                .lowerSelfReference()
                .updateContext(myAstContext)
                .lowerTypeAliases(myAstContext)
                .lowerOverrides(myAstContext)
                .specifyDynamicTypes()
                .lowerConstructors()
    }
}
