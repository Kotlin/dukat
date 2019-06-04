package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.compiler.lowerPrimitives
import org.jetbrains.dukat.compiler.lowerings.escapeIdentificators
import org.jetbrains.dukat.compiler.lowerings.filterOutNonDeclarations
import org.jetbrains.dukat.compiler.lowerings.introduceMissedOverloads
import org.jetbrains.dukat.compiler.lowerings.introduceModels
import org.jetbrains.dukat.compiler.lowerings.rearrangeConstructors
import org.jetbrains.dukat.nodeIntroduction.lowerIntersectionType
import org.jetbrains.dukat.compiler.lowerings.lowerNullable
import org.jetbrains.dukat.compiler.lowerings.lowerOverrides
import org.jetbrains.dukat.compiler.lowerings.lowerVarargs
import org.jetbrains.dukat.compiler.lowerings.merge.mergeClassLikesAndModuleDeclarations
import org.jetbrains.dukat.compiler.lowerings.merge.mergeClassesAndInterfaces
import org.jetbrains.dukat.compiler.lowerings.merge.mergeModules
import org.jetbrains.dukat.compiler.lowerings.merge.mergeNestedClasses
import org.jetbrains.dukat.compiler.lowerings.merge.mergeVarsAndInterfaces
import org.jetbrains.dukat.compiler.lowerings.merge.specifyTypeNodesWithModuleData
import org.jetbrains.dukat.compiler.lowerings.model.addStandardImports
import org.jetbrains.dukat.compiler.lowerings.model.omitStdLib
import org.jetbrains.dukat.compiler.lowerings.rearrangeGeneratedEntities
import org.jetbrains.dukat.compiler.lowerings.specifyUnionType
import org.jetbrains.dukat.compiler.lowerings.typeAlias.resolveTypeAliases
import org.jetbrains.dukat.nodeIntroduction.introduceExportAnnotations
import org.jetbrains.dukat.nodeIntroduction.introduceNodes
import org.jetbrains.dukat.nodeIntroduction.introduceQualifiedNode
import org.jetbrains.dukat.nodeIntroduction.introduceTupleNodes
import org.jetbrains.dukat.nodeIntroduction.introduceTypeNodes
import org.jetbrains.dukat.nodeIntroduction.lowerThisType
import org.jetbrains.dukat.nodeIntroduction.lowerUnionType
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.lowerings.desugarArrayDeclarations
import org.jetbrains.dukat.tsmodel.lowerings.eliminateStringType
import org.jetbrains.dukat.tsmodel.lowerings.generateInterfaceReferences
import org.jetbrains.dukat.tsmodel.lowerings.resolvePartials


interface TypescriptInputTranslator : InputTranslator {
    fun translateFile(fileName: String, packageName: NameEntity): SourceSetDeclaration
    fun release()

    override fun translate(fileName: String, packageName: NameEntity): SourceSetModel {
        return lower(translateFile(fileName, packageName))
    }

    private fun lower(documentRoot: SourceSetDeclaration): SourceSetModel {
        return documentRoot
                .filterOutNonDeclarations()
                .resolvePartials()
                .generateInterfaceReferences()
                .eliminateStringType()
                .desugarArrayDeclarations()
                .introduceNodes()
                .introduceTypeNodes()
                .introduceQualifiedNode()
                .introduceTupleNodes()
                .introduceExportAnnotations()
                .lowerUnionType()
                .lowerNullable()
                .lowerPrimitives()
                .escapeIdentificators()
                .lowerVarargs()
                .lowerIntersectionType()
                .lowerThisType()
                .lowerOverrides()
                .resolveTypeAliases()
                .specifyUnionType()
                .rearrangeGeneratedEntities()
                .rearrangeConstructors()
                .introduceMissedOverloads()
                .introduceModels()
                .mergeModules()
                .mergeClassesAndInterfaces()
                .mergeClassLikesAndModuleDeclarations()
                .mergeVarsAndInterfaces()
                .mergeNestedClasses()
                .specifyTypeNodesWithModuleData()
                .addStandardImports()
                .omitStdLib()
    }
}