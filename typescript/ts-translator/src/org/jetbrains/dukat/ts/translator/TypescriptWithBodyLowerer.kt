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
import org.jetbrains.dukat.compiler.lowerPrimitives
import org.jetbrains.dukat.model.commonLowerings.addNoinlineModifier
import org.jetbrains.dukat.model.commonLowerings.addStandardImportsAndAnnotations
import org.jetbrains.dukat.model.commonLowerings.escapeIdentificators
import org.jetbrains.dukat.model.commonLowerings.lowerOverrides
import org.jetbrains.dukat.model.commonLowerings.removeConflictingOverloads
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.nodeIntroduction.introduceNodes
import org.jetbrains.dukat.nodeIntroduction.introduceQualifiedNode
import org.jetbrains.dukat.nodeIntroduction.introduceTypeNodes
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
import org.jetrbains.dukat.nodeLowering.lowerings.lowerNullable
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
            //.filterOutNonDeclarations()
            .syncTypeNames(renameMap)
            .renameImpossibleDeclarations()
            .resolveTypescriptUtilityTypes()
            .resolveDefaultTypeParams()
            .generateInterfaceReferences()
            .eliminateStringType()
            .desugarArrayDeclarations()
            .fixImpossibleInheritance()
            .lowerPartialOfT()

        val nodes = declarations.introduceNodes(moduleNameResolver)
            .introduceTypeNodes()
            .introduceQualifiedNode()
            .resolveModuleAnnotations()
            .lowerNullable()
            .lowerPrimitives()
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
            .lowerOverrides(stdLibSourceSet)
            .specifyTypeNodesWithModuleData()
            .addExplicitGettersAndSetters()
            .addImports()
            .anyfyUnresolvedTypes()
            .addNoinlineModifier()
            .addStandardImportsAndAnnotations()

        return models
    }

    override fun lower(sourceBundle: SourceBundleDeclaration): SourceBundleModel {
        var stdLib: SourceSetModel? = null
        val sources = mutableListOf<SourceSetDeclaration>()

        val renameMap: MutableMap<String, NameEntity> = mutableMapOf()
        val uidToFqNameMapper: MutableMap<String, FqNode> = mutableMapOf()

        sourceBundle.sources.forEach { source ->
            if ((source.isStdLib()) && (stdLib == null)) {
                val stdSourceSet = source.renameStdLibEntities() { uid, newName ->
                    renameMap[uid] = newName
                }
                stdLib = lower(stdSourceSet, null, renameMap, uidToFqNameMapper)
            } else {
                sources.add(source)
            }
        }

        val loweredSources = sources.map { source ->
            lower(source, stdLib, renameMap, uidToFqNameMapper.toMutableMap())
        }.toMutableList()


        stdLib = stdLib
            ?.whiteList(TS_STDLIB_WHITE_LIST)
        //?.splitIntoSeparateEntities()

        stdLib?.let { loweredSources.add(it) }

        return SourceBundleModel(loweredSources)
    }
}