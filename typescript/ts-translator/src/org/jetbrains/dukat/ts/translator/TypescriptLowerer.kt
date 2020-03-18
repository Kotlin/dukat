package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.commonLowerings.AddExplicitGettersAndSetters
import org.jetbrains.dukat.commonLowerings.AddImports
import org.jetbrains.dukat.commonLowerings.AnyfyUnresolvedTypes
import org.jetbrains.dukat.commonLowerings.ExtractNonExternalDeclarations
import org.jetbrains.dukat.commonLowerings.RemoveUnsupportedJsNames
import org.jetbrains.dukat.commonLowerings.SubstituteTsStdLibEntities
import org.jetbrains.dukat.commonLowerings.merge.MergeClassLike
import org.jetbrains.dukat.commonLowerings.merge.MergeClassLikesAndModuleDeclarations
import org.jetbrains.dukat.commonLowerings.merge.MergeModules
import org.jetbrains.dukat.commonLowerings.merge.MergeNestedClasses
import org.jetbrains.dukat.commonLowerings.merge.MergeVarsAndInterfaces
import org.jetbrains.dukat.commonLowerings.merge.SpecifyTypeNodesWithModuleData
import org.jetbrains.dukat.model.commonLowerings.AddNoinlineModifier
import org.jetbrains.dukat.model.commonLowerings.AddStandardImportsAndAnnotations
import org.jetbrains.dukat.model.commonLowerings.EscapeIdentificators
import org.jetbrains.dukat.model.commonLowerings.LowerOverrides
import org.jetbrains.dukat.model.commonLowerings.RemoveConflictingOverloads
import org.jetbrains.dukat.model.commonLowerings.RemoveRedundantInlineFunction
import org.jetbrains.dukat.model.commonLowerings.lower
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.nodeIntroduction.introduceNodes
import org.jetbrains.dukat.nodeIntroduction.lowerIntersectionType
import org.jetbrains.dukat.nodeIntroduction.lowerThisType
import org.jetbrains.dukat.nodeIntroduction.resolveModuleAnnotations
import org.jetbrains.dukat.tsLowerings.AddPackageName
import org.jetbrains.dukat.tsLowerings.DesugarArrayDeclarations
import org.jetbrains.dukat.tsLowerings.EliminateStringType
import org.jetbrains.dukat.tsLowerings.FilterOutNonDeclarations
import org.jetbrains.dukat.tsLowerings.FixImpossibleInheritance
import org.jetbrains.dukat.tsLowerings.GenerateInterfaceReferences
import org.jetbrains.dukat.tsLowerings.LowerPartialOf
import org.jetbrains.dukat.tsLowerings.LowerPrimitives
import org.jetbrains.dukat.tsLowerings.MergeParentsForMergedInterfaces
import org.jetbrains.dukat.tsLowerings.RenameImpossibleDeclarations
import org.jetbrains.dukat.tsLowerings.ResolveDefaultTypeParams
import org.jetbrains.dukat.tsLowerings.ResolveTypescriptUtilityTypes
import org.jetbrains.dukat.tsLowerings.SyncTypeNames
import org.jetbrains.dukat.tsLowerings.lower
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

open class TypescriptLowerer(
        private val moduleNameResolver: ModuleNameResolver,
        private val packageName: NameEntity?
) : ECMAScriptLowerer {
    override fun lower(sourceSet: SourceSetDeclaration, stdLibSourceSet: SourceSetModel?, renameMap: Map<String, NameEntity>, uidToFqNameMapper: MutableMap<String, FqNode>): SourceSetModel {
        val declarations = sourceSet
                .lower(
                    AddPackageName(packageName),
                    MergeParentsForMergedInterfaces(),
                    FilterOutNonDeclarations(),
                    SyncTypeNames(renameMap),
                    RenameImpossibleDeclarations(),
                    ResolveTypescriptUtilityTypes(),
                    ResolveDefaultTypeParams(),
                    LowerPrimitives(),
                    GenerateInterfaceReferences(),
                    EliminateStringType(),
                    DesugarArrayDeclarations(),
                    FixImpossibleInheritance(),
                    LowerPartialOf()
                )


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
                .lower(
                    RemoveConflictingOverloads(),
                    SubstituteTsStdLibEntities(),
                    EscapeIdentificators(),
                    RemoveUnsupportedJsNames(),
                    MergeClassLike(),
                    MergeModules(),
                    MergeClassLikesAndModuleDeclarations(),
                    MergeVarsAndInterfaces(),
                    MergeNestedClasses(),
                    ExtractNonExternalDeclarations(),
                    LowerOverrides(),
                    SpecifyTypeNodesWithModuleData(),
                    AddExplicitGettersAndSetters(),
                    AddImports(),
                    AnyfyUnresolvedTypes(),
                    AddNoinlineModifier(),
                    AddStandardImportsAndAnnotations(),
                    RemoveRedundantInlineFunction()
                )

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