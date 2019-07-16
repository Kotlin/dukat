package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.commonLowerings.lowerOverrides
import org.jetbrains.dukat.compiler.lowerPrimitives
import org.jetbrains.dukat.commonLowerings.merge.escapeIdentificators
import org.jetbrains.dukat.compiler.lowerings.filterOutNonDeclarations
import org.jetbrains.dukat.compiler.lowerings.introduceMissedOverloads
import org.jetbrains.dukat.compiler.lowerings.introduceModels
import org.jetbrains.dukat.compiler.lowerings.lowerNullable
import org.jetbrains.dukat.compiler.lowerings.lowerVarargs
import org.jetbrains.dukat.commonLowerings.merge.mergeClassLikesAndModuleDeclarations
import org.jetbrains.dukat.commonLowerings.merge.mergeClassesAndInterfaces
import org.jetbrains.dukat.commonLowerings.merge.mergeModules
import org.jetbrains.dukat.commonLowerings.merge.mergeNestedClasses
import org.jetbrains.dukat.commonLowerings.merge.mergeVarsAndInterfaces
import org.jetbrains.dukat.commonLowerings.merge.mergeWithNameSpace
import org.jetbrains.dukat.commonLowerings.merge.specifyTypeNodesWithModuleData
import org.jetbrains.dukat.commonLowerings.addStandardImportsAndAnnotations
import org.jetbrains.dukat.commonLowerings.omitStdLib
import org.jetbrains.dukat.compiler.lowerings.moveTypeAliasesOutside
import org.jetbrains.dukat.compiler.lowerings.rearrangeConstructors
import org.jetbrains.dukat.compiler.lowerings.rearrangeGeneratedEntities
import org.jetbrains.dukat.compiler.lowerings.specifyUnionType
import org.jetbrains.dukat.compiler.lowerings.typeAlias.resolveTypeAliases
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.nodeIntroduction.introduceNodes
import org.jetbrains.dukat.nodeIntroduction.introduceQualifiedNode
import org.jetbrains.dukat.nodeIntroduction.introduceTupleNodes
import org.jetbrains.dukat.nodeIntroduction.introduceTypeNodes
import org.jetbrains.dukat.nodeIntroduction.lowerIntersectionType
import org.jetbrains.dukat.nodeIntroduction.lowerThisType
import org.jetbrains.dukat.nodeIntroduction.lowerUnionType
import org.jetbrains.dukat.nodeIntroduction.resolveModuleAnnotations
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.lowerings.desugarArrayDeclarations
import org.jetbrains.dukat.tsmodel.lowerings.eliminateStringType
import org.jetbrains.dukat.tsmodel.lowerings.generateInterfaceReferences
import org.jetbrains.dukat.tsmodel.lowerings.resolvePartials


interface TypescriptInputTranslator : InputTranslator {
    fun translateFile(fileName: String, packageName: NameEntity): SourceSetDeclaration
    fun release()

    val packageName: NameEntity
    val moduleNameResolver: ModuleNameResolver

    override fun translate(fileName: String): SourceSetModel {
        return lower(translateFile(fileName, packageName))
    }

    private fun lower(documentRoot: SourceSetDeclaration): SourceSetModel {
        return documentRoot
                .filterOutNonDeclarations()
                .resolvePartials()
                .generateInterfaceReferences()
                .eliminateStringType()
                .desugarArrayDeclarations()
                .introduceNodes(moduleNameResolver)
                .introduceTypeNodes()
                .introduceQualifiedNode()
                .introduceTupleNodes()
                .resolveModuleAnnotations()
                .lowerUnionType()
                .lowerNullable()
                .lowerPrimitives()
                .lowerVarargs()
                .lowerIntersectionType()
                .lowerThisType()
                .resolveTypeAliases()
                .specifyUnionType()
                .rearrangeGeneratedEntities()
                .rearrangeConstructors()
                .introduceMissedOverloads()
                .moveTypeAliasesOutside()
                .introduceModels()
                .escapeIdentificators()
                .mergeModules()
                .mergeWithNameSpace()
                .mergeClassesAndInterfaces()
                .mergeClassLikesAndModuleDeclarations()
                .mergeVarsAndInterfaces()
                .mergeNestedClasses()
                .lowerOverrides()
                .specifyTypeNodesWithModuleData()
                .addStandardImportsAndAnnotations()
                .omitStdLib()
    }
}