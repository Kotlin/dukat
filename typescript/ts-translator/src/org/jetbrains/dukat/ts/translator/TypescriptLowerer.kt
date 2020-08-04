package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.commonLowerings.AddExplicitGettersAndSetters
import org.jetbrains.dukat.commonLowerings.AddImports
import org.jetbrains.dukat.commonLowerings.AnyfyUnresolvedTypes
import org.jetbrains.dukat.commonLowerings.RemoveDuplicateMembers
import org.jetbrains.dukat.commonLowerings.RemoveParentAny
import org.jetbrains.dukat.commonLowerings.RemoveUnsupportedJsNames
import org.jetbrains.dukat.commonLowerings.SeparateNonExternalEntities
import org.jetbrains.dukat.commonLowerings.SubstituteTsStdLibEntities
import org.jetbrains.dukat.commonLowerings.merge.MergeClassLikesAndModuleDeclarations
import org.jetbrains.dukat.commonLowerings.merge.MergeVarsAndInterfaces
import org.jetbrains.dukat.model.commonLowerings.AddStandardImportsAndAnnotations
import org.jetbrains.dukat.model.commonLowerings.CorrectStdLibTypes
import org.jetbrains.dukat.model.commonLowerings.EscapeIdentificators
import org.jetbrains.dukat.model.commonLowerings.IntroduceAmbiguousInterfaceMembers
import org.jetbrains.dukat.model.commonLowerings.LowerOverrides
import org.jetbrains.dukat.model.commonLowerings.ModelContextAwareLowering
import org.jetbrains.dukat.model.commonLowerings.RearrangeConstructors
import org.jetbrains.dukat.model.commonLowerings.RemoveConflictingOverloads
import org.jetbrains.dukat.model.commonLowerings.RemoveKotlinBuiltIns
import org.jetbrains.dukat.model.commonLowerings.RemoveRedundantTypeParams
import org.jetbrains.dukat.model.commonLowerings.lower
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.nodeIntroduction.IntroduceNodes
import org.jetbrains.dukat.nodeIntroduction.ResolveModuleAnnotations
import org.jetbrains.dukat.tsLowerings.AddPackageName
import org.jetbrains.dukat.tsLowerings.ConvertKeyOfsAndLookups
import org.jetbrains.dukat.tsLowerings.DesugarArrayDeclarations
import org.jetbrains.dukat.tsLowerings.EscapeLiterals
import org.jetbrains.dukat.tsLowerings.FilterOutNonDeclarations
import org.jetbrains.dukat.tsLowerings.FixImpossibleInheritance
import org.jetbrains.dukat.tsLowerings.GenerateInterfaceReferences
import org.jetbrains.dukat.tsLowerings.IntroduceSyntheticExportModifiers
import org.jetbrains.dukat.tsLowerings.LowerPartialOf
import org.jetbrains.dukat.tsLowerings.LowerPrimitives
import org.jetbrains.dukat.tsLowerings.LowerThisType
import org.jetbrains.dukat.tsLowerings.MergeClassLikes
import org.jetbrains.dukat.tsLowerings.MergeModules
import org.jetbrains.dukat.tsLowerings.MoveAliasesFromMergeableModules
import org.jetbrains.dukat.tsLowerings.PreprocessUnionTypes
import org.jetbrains.dukat.tsLowerings.ProcessForOfStatements
import org.jetbrains.dukat.tsLowerings.ProcessNullabilityChecks
import org.jetbrains.dukat.tsLowerings.RemoveThisParameters
import org.jetbrains.dukat.tsLowerings.RemoveUnusedGeneratedEntities
import org.jetbrains.dukat.tsLowerings.RenameImpossibleDeclarations
import org.jetbrains.dukat.tsLowerings.ResolveCollections
import org.jetbrains.dukat.tsLowerings.ResolveDefaultTypeParams
import org.jetbrains.dukat.tsLowerings.ResolveLambdaParents
import org.jetbrains.dukat.tsLowerings.ResolveLoops
import org.jetbrains.dukat.tsLowerings.ResolveTypeAliases
import org.jetbrains.dukat.tsLowerings.ResolveTypescriptUtilityTypes
import org.jetbrains.dukat.tsLowerings.SpecifyUnionType
import org.jetbrains.dukat.tsLowerings.lower
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetrbains.dukat.nodeLowering.lowerings.introduceModels
import org.jetrbains.dukat.nodeLowering.lowerings.lower

open class TypescriptLowerer(
        private val moduleNameResolver: ModuleNameResolver,
        private val packageName: NameEntity?
) : ECMAScriptLowerer {
    override fun lower(sourceSet: SourceSetDeclaration): SourceSetModel {
        val declarations = sourceSet
                .lower(
                        AddPackageName(packageName),
                        RemoveThisParameters(),
                        MergeModules(),
                        MergeClassLikes(),
                        IntroduceSyntheticExportModifiers(),
                        ResolveLambdaParents(),
                        FilterOutNonDeclarations(),
                        RenameImpossibleDeclarations(),
                        ResolveTypescriptUtilityTypes(),
                        ResolveDefaultTypeParams(),
                        ConvertKeyOfsAndLookups(),
                        LowerPrimitives(),
                        GenerateInterfaceReferences(),
                        DesugarArrayDeclarations(),
                        FixImpossibleInheritance(),
                        LowerPartialOf(),
                        ResolveLoops(),
                        ResolveCollections(),
                        LowerThisType(),
                        ResolveTypeAliases(),
                        PreprocessUnionTypes(),
                        SpecifyUnionType(),
                        RemoveUnusedGeneratedEntities(),
                        ProcessForOfStatements(),
                        ProcessNullabilityChecks(),
                        EscapeLiterals(),
                        MoveAliasesFromMergeableModules()
                )


        val nodes = IntroduceNodes()
                .lower(declarations)
                .lower(
                        ResolveModuleAnnotations(moduleNameResolver)
                )

        val models = nodes
                .introduceModels()
                .lower(
                        RearrangeConstructors(),
                        RemoveRedundantTypeParams(),
                        RemoveConflictingOverloads(),
                        SubstituteTsStdLibEntities(),
                        RemoveParentAny(),
                        EscapeIdentificators(),
                        RemoveUnsupportedJsNames(),
                        MergeClassLikesAndModuleDeclarations(),
                        MergeVarsAndInterfaces(),
                        SeparateNonExternalEntities(),
                        ModelContextAwareLowering()
                                .lower { context, inheritanceContext ->
                                    IntroduceAmbiguousInterfaceMembers(context, inheritanceContext)
                                }
                                .lower { context, inheritanceContext ->
                                    LowerOverrides(context, inheritanceContext)
                                },
                        AddExplicitGettersAndSetters(),
                        AnyfyUnresolvedTypes(),
                        RemoveKotlinBuiltIns(),
                        CorrectStdLibTypes(),
                        RemoveDuplicateMembers(),
                        RearrangeConstructors(),
                        AddImports(),
                        AddStandardImportsAndAnnotations()
                )

        return models
    }
}