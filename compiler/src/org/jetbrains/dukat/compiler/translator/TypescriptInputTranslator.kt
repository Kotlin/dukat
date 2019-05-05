package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.compiler.lowerPrimitives
import org.jetbrains.dukat.compiler.lowerings.escapeIdentificators
import org.jetbrains.dukat.compiler.lowerings.filterOutNonDeclarations
import org.jetbrains.dukat.compiler.lowerings.introduceExportAnnotations
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
import org.jetbrains.dukat.compiler.lowerings.model.addStandardImports
import org.jetbrains.dukat.compiler.lowerings.nodeIntroduction.introduceNodes
import org.jetbrains.dukat.compiler.lowerings.nodeIntroduction.introduceTypeNodes
import org.jetbrains.dukat.compiler.lowerings.rearrangeGeneratedEntities
import org.jetbrains.dukat.compiler.lowerings.specifyUnionType
import org.jetbrains.dukat.compiler.lowerings.typeAlias.resolveTypeAliases
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.lowerings.desugarArrayDeclarations
import org.jetbrains.dukat.tsmodel.lowerings.eliminateStringType
import org.jetbrains.dukat.tsmodel.lowerings.generateInterfaceReferences


interface TypescriptInputTranslator : InputTranslator {
    fun translateFile(fileName: String): SourceSetDeclaration
    fun release()

    override fun translate(fileName: String): SourceSetModel {
        return lower(translateFile(fileName))
    }

    private fun lower(documentRoot: SourceSetDeclaration): SourceSetModel {
        return documentRoot
                .filterOutNonDeclarations()
                .generateInterfaceReferences()
                .eliminateStringType()
                .desugarArrayDeclarations()
                .introduceNodes()
                .introduceTypeNodes()
                .introduceQualifiedNode()
                .introduceExportAnnotations()
                .lowerNullable()
                .lowerPrimitives()
                .escapeIdentificators()
                .lowerUnionType()
                .lowerVarargs()
                .lowerIntersectionType()
                .lowerThisType()
                .lowerOverrides()
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
                .addStandardImports()
    }
}