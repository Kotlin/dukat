package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.commonLowerings.addExplicitGettersAndSetters
import org.jetbrains.dukat.commonLowerings.addImports
import org.jetbrains.dukat.commonLowerings.anyfyUnresolvedTypes
import org.jetbrains.dukat.commonLowerings.extractNonExternalDeclarations
import org.jetbrains.dukat.commonLowerings.merge.mergeClassLikes
import org.jetbrains.dukat.commonLowerings.merge.mergeClassLikesAndModuleDeclarations
import org.jetbrains.dukat.commonLowerings.merge.mergeModules
import org.jetbrains.dukat.commonLowerings.merge.mergeNestedClasses
import org.jetbrains.dukat.commonLowerings.merge.mergeVarsAndInterfaces
import org.jetbrains.dukat.commonLowerings.merge.specifyTypeNodesWithModuleData
import org.jetbrains.dukat.commonLowerings.removeUnsupportedJsNames
import org.jetbrains.dukat.commonLowerings.substituteTsStdLibEntities
import org.jetbrains.dukat.commonLowerings.whiteList
import org.jetbrains.dukat.model.commonLowerings.addNoinlineModifier
import org.jetbrains.dukat.model.commonLowerings.addStandardImportsAndAnnotations
import org.jetbrains.dukat.model.commonLowerings.escapeIdentificators
import org.jetbrains.dukat.model.commonLowerings.lowerOverrides
import org.jetbrains.dukat.model.commonLowerings.removeConflictingOverloads
import org.jetbrains.dukat.model.commonLowerings.removeRedundantInlineFunction
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.nodeIntroduction.introduceNodes
import org.jetbrains.dukat.nodeIntroduction.lowerIntersectionType
import org.jetbrains.dukat.nodeIntroduction.lowerThisType
import org.jetbrains.dukat.nodeIntroduction.resolveModuleAnnotations
import org.jetbrains.dukat.stdlib.org.jetbrains.dukat.stdlib.TS_STDLIB_WHITE_LIST
import org.jetbrains.dukat.tsLowerings.addPackageName
import org.jetbrains.dukat.tsLowerings.desugarArrayDeclarations
import org.jetbrains.dukat.tsLowerings.eliminateStringType
import org.jetbrains.dukat.tsLowerings.filterOutNonDeclarations
import org.jetbrains.dukat.tsLowerings.fixImpossibleInheritance
import org.jetbrains.dukat.tsLowerings.generateInterfaceReferences
import org.jetbrains.dukat.tsLowerings.lowerPartialOfT
import org.jetbrains.dukat.tsLowerings.lowerPrimitives
import org.jetbrains.dukat.tsLowerings.mergeParentsForMergedInterfaces
import org.jetbrains.dukat.tsLowerings.renameImpossibleDeclarations
import org.jetbrains.dukat.tsLowerings.renameStdLibEntities
import org.jetbrains.dukat.tsLowerings.resolveDefaultTypeParams
import org.jetbrains.dukat.tsLowerings.resolveTypescriptUtilityTypes
import org.jetbrains.dukat.tsLowerings.syncTypeNames
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetrbains.dukat.nodeLowering.lowerings.FqNode
import org.jetrbains.dukat.nodeLowering.lowerings.introduceMissedOverloads
import org.jetrbains.dukat.nodeLowering.lowerings.introduceModels
import org.jetrbains.dukat.nodeLowering.lowerings.lowerVarargs
import org.jetrbains.dukat.nodeLowering.lowerings.rearrangeConstructors
import org.jetrbains.dukat.nodeLowering.lowerings.removeUnusedGeneratedEntities
import org.jetrbains.dukat.nodeLowering.lowerings.specifyUnionType
import org.jetrbains.dukat.nodeLowering.lowerings.typeAlias.resolveTypeAliases

open class TypescriptWithBodyLowerer(
    private val moduleNameResolver: ModuleNameResolver,
    private val packageName: NameEntity?
) : ECMAScriptLowerer {
    override fun lower(
        sourceSet: SourceSetDeclaration,
        stdLibSourceSet: SourceSetModel?,
        renameMap: Map<String, NameEntity>,
        uidToFqNameMapper: MutableMap<String, FqNode>
    ): SourceSetModel {

        val declarations = sourceSet
            .addPackageName(packageName)
            .mergeParentsForMergedInterfaces()
            //.filterOutNonDeclarations()
            .syncTypeNames(renameMap)
            .renameImpossibleDeclarations()
            .resolveTypescriptUtilityTypes()
            .resolveDefaultTypeParams()
            .lowerPrimitives()
            .generateInterfaceReferences()
            .eliminateStringType()
            .desugarArrayDeclarations()
            .fixImpossibleInheritance()
            .lowerPartialOfT()


        val nodes = declarations.introduceNodes(moduleNameResolver)
            .resolveModuleAnnotations()
            .lowerVarargs()
            .lowerIntersectionType()
            .lowerThisType()
            .resolveTypeAliases()
            .specifyUnionType()
            .removeUnusedGeneratedEntities()
            .rearrangeConstructors()
            .introduceMissedOverloads()

        val models = nodes
            .introduceModels(uidToFqNameMapper)
            .removeConflictingOverloads()
            .substituteTsStdLibEntities()
            .escapeIdentificators()
            .removeUnsupportedJsNames()
            .mergeClassLikes()
            .mergeModules()
            .mergeClassLikesAndModuleDeclarations()
            .mergeVarsAndInterfaces()
            .mergeNestedClasses()
            .extractNonExternalDeclarations()
            .lowerOverrides()
            .specifyTypeNodesWithModuleData()
            .addExplicitGettersAndSetters()
            .addImports()
            .anyfyUnresolvedTypes()
            .addNoinlineModifier()
            .addStandardImportsAndAnnotations()
            .removeRedundantInlineFunction()

        return models
    }

    override fun lower(sourceBundle: SourceBundleDeclaration): SourceBundleModel {
        val renameMap: MutableMap<String, NameEntity> = mutableMapOf()
        val uidToFqNameMapper: MutableMap<String, FqNode> = mutableMapOf()

        return SourceBundleModel(sources = sourceBundle.sources.map {
            lower(it, null, renameMap, uidToFqNameMapper.toMutableMap())
        })
    }
}