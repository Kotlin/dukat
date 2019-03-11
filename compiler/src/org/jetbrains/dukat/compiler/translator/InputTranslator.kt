package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.ast.model.model.SourceSetModel
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.compiler.AstContext
import org.jetbrains.dukat.compiler.lowerPrimitives
import org.jetbrains.dukat.compiler.lowerings.escapeIdentificators
import org.jetbrains.dukat.compiler.lowerings.filterOutNonDeclarations
import org.jetbrains.dukat.compiler.lowerings.introduceExports
import org.jetbrains.dukat.compiler.lowerings.introduceMissedOverloads
import org.jetbrains.dukat.compiler.lowerings.introduceQualifiedNode
import org.jetbrains.dukat.compiler.lowerings.introduceRepresentationModels
import org.jetbrains.dukat.compiler.lowerings.lowerConstructors
import org.jetbrains.dukat.compiler.lowerings.lowerIntersectionType
import org.jetbrains.dukat.compiler.lowerings.lowerNullable
import org.jetbrains.dukat.compiler.lowerings.lowerOverrides
import org.jetbrains.dukat.compiler.lowerings.lowerThisType
import org.jetbrains.dukat.compiler.lowerings.lowerUnionType
import org.jetbrains.dukat.compiler.lowerings.lowerVarargs
import org.jetbrains.dukat.compiler.lowerings.merge.mergeClassLikesAndModuleDeclarations
import org.jetbrains.dukat.compiler.lowerings.merge.mergeClassesAndInterfaces
import org.jetbrains.dukat.compiler.lowerings.merge.mergeModules
import org.jetbrains.dukat.compiler.lowerings.merge.mergeNestedClasses
import org.jetbrains.dukat.compiler.lowerings.merge.mergeVarsAndInterfaces
import org.jetbrains.dukat.compiler.lowerings.merge.specifyTypeNodesWithModuleData
import org.jetbrains.dukat.compiler.lowerings.nodeIntroduction.introduceModuleMetadata
import org.jetbrains.dukat.compiler.lowerings.nodeIntroduction.introduceNodes
import org.jetbrains.dukat.compiler.lowerings.nodeIntroduction.introduceTypeNodes
import org.jetbrains.dukat.compiler.lowerings.rearrangeGeneratedEntities
import org.jetbrains.dukat.compiler.lowerings.specifyUnionType
import org.jetbrains.dukat.compiler.lowerings.typeAlias.resolveTypeAliases
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.lowerings.desugarArrayDeclarations
import org.jetbrains.dukat.tsmodel.lowerings.eliminateStringType
import org.jetbrains.dukat.tsmodel.lowerings.generateInterfaceReferences

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
    }

    return this
}

private fun SourceSetNode.updateContext(astContext: AstContext) = transform { it.updateContext(astContext) }

interface InputTranslator {
    fun translateFile(fileName: String): SourceSetDeclaration
    fun release()

    fun lower(documentRoot: SourceSetDeclaration): SourceSetModel {
        val myAstContext = AstContext()

        return documentRoot
                .filterOutNonDeclarations()
                .generateInterfaceReferences()
                .eliminateStringType()
                .desugarArrayDeclarations()
                .introduceNodes()
                .introduceModuleMetadata()
                .introduceTypeNodes()
                .introduceQualifiedNode()
                .introduceExports()
                .lowerNullable()
                .lowerPrimitives()
                .escapeIdentificators()
                .lowerUnionType()
                .lowerVarargs()
                .lowerIntersectionType()
                .lowerThisType()
                .updateContext(myAstContext)
                .lowerOverrides(myAstContext)
                .resolveTypeAliases()
                .specifyUnionType()
                .rearrangeGeneratedEntities()
                .lowerConstructors()
                .introduceMissedOverloads()
                .introduceRepresentationModels()
                .mergeModules()
                .mergeClassesAndInterfaces()
                .mergeClassLikesAndModuleDeclarations()
                .mergeVarsAndInterfaces()
                .mergeNestedClasses()
                .specifyTypeNodesWithModuleData()
    }
}