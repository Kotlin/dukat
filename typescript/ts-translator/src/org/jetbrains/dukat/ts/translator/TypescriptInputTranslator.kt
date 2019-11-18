package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.commonLowerings.addExplicitGettersAndSetters
import org.jetbrains.dukat.commonLowerings.filterOutKotlinStdEntities
import org.jetbrains.dukat.commonLowerings.merge.mergeClassLikesAndModuleDeclarations
import org.jetbrains.dukat.commonLowerings.merge.mergeClassesAndInterfaces
import org.jetbrains.dukat.commonLowerings.merge.mergeModules
import org.jetbrains.dukat.commonLowerings.merge.mergeNestedClasses
import org.jetbrains.dukat.commonLowerings.merge.mergeVarsAndInterfaces
import org.jetbrains.dukat.commonLowerings.merge.mergeWithNameSpace
import org.jetbrains.dukat.commonLowerings.merge.specifyTypeNodesWithModuleData
import org.jetbrains.dukat.commonLowerings.removeUnsupportedJsNames
import org.jetbrains.dukat.compiler.lowerPrimitives
import org.jetbrains.dukat.model.commonLowerings.addStandardImportsAndAnnotations
import org.jetbrains.dukat.model.commonLowerings.escapeIdentificators
import org.jetbrains.dukat.model.commonLowerings.lowerOverrides
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.nodeIntroduction.introduceNodes
import org.jetbrains.dukat.nodeIntroduction.introduceQualifiedNode
import org.jetbrains.dukat.nodeIntroduction.introduceTypeNodes
import org.jetbrains.dukat.nodeIntroduction.lowerIntersectionType
import org.jetbrains.dukat.nodeIntroduction.lowerThisType
import org.jetbrains.dukat.nodeIntroduction.resolveModuleAnnotations
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.tsLowerings.desugarArrayDeclarations
import org.jetbrains.dukat.tsLowerings.eliminateStringType
import org.jetbrains.dukat.tsLowerings.filterOutNonDeclarations
import org.jetbrains.dukat.tsLowerings.generateInterfaceReferences
import org.jetbrains.dukat.tsLowerings.lowerPartialOfT
import org.jetbrains.dukat.tsLowerings.resolveDefaultTypeParams
import org.jetbrains.dukat.tsLowerings.resolveTypescriptUtilityTypes
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetrbains.dukat.nodeLowering.lowerings.introduceMissedOverloads
import org.jetrbains.dukat.nodeLowering.lowerings.introduceModels
import org.jetrbains.dukat.nodeLowering.lowerings.lowerNullable
import org.jetrbains.dukat.nodeLowering.lowerings.lowerVarargs
import org.jetrbains.dukat.nodeLowering.lowerings.moveTypeAliasesOutside
import org.jetrbains.dukat.nodeLowering.lowerings.rearrangeConstructors
import org.jetrbains.dukat.nodeLowering.lowerings.removeUnusedGeneratedEntities
import org.jetrbains.dukat.nodeLowering.lowerings.specifyUnionType
import org.jetrbains.dukat.nodeLowering.lowerings.typeAlias.resolveTypeAliases
import substituteTsStdLibEntities

fun SourceSetDeclaration.isStdLib(): Boolean {
    return sourceName == "<LIBROOT>"
}

interface TypescriptInputTranslator<T> : InputTranslator<T> {
    val moduleNameResolver: ModuleNameResolver

    fun lower(sourceSet: SourceSetDeclaration, stdLibSourceSet: SourceSetModel?): SourceSetModel {
        val declarations = sourceSet
                .filterOutNonDeclarations()
                .substituteTsStdLibEntities()
                .resolveTypescriptUtilityTypes()
                .resolveDefaultTypeParams()
                .generateInterfaceReferences()
                .eliminateStringType()
                .desugarArrayDeclarations()
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
                .moveTypeAliasesOutside()

        val models = nodes
                .introduceModels()
                .escapeIdentificators()
                .removeUnsupportedJsNames()
                .mergeModules()
                .mergeWithNameSpace()
                .mergeClassesAndInterfaces()
                .mergeClassLikesAndModuleDeclarations()
                .mergeVarsAndInterfaces()
                .mergeNestedClasses()
                .lowerOverrides(stdLibSourceSet)
                .specifyTypeNodesWithModuleData()
                .addExplicitGettersAndSetters()
                .addStandardImportsAndAnnotations()

        return models
    }

    fun lower(sourceBundle: SourceBundleDeclaration): SourceBundleModel {
        var stdLib: SourceSetModel? = null
        val sources = mutableListOf<SourceSetDeclaration>()

        sourceBundle.sources.forEach { source ->
            if ((source.isStdLib()) && (stdLib == null)) {
                stdLib = lower(source, null)
            } else {
                sources.add(source)
            }
        }

        val loweredSources = sources.map { source -> lower(source, stdLib) }

        stdLib?.filterOutKotlinStdEntities()

        return SourceBundleModel(loweredSources)
    }
}