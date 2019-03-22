package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.ast.model.model.SourceSetModel
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
import org.jetbrains.dukat.compiler.lowerings.nodeIntroduction.introduceNodes
import org.jetbrains.dukat.compiler.lowerings.nodeIntroduction.introduceTypeNodes
import org.jetbrains.dukat.compiler.lowerings.rearrangeGeneratedEntities
import org.jetbrains.dukat.compiler.lowerings.specifyUnionType
import org.jetbrains.dukat.compiler.lowerings.typeAlias.resolveTypeAliases
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.lowerings.desugarArrayDeclarations
import org.jetbrains.dukat.tsmodel.lowerings.eliminateStringType
import org.jetbrains.dukat.tsmodel.lowerings.generateInterfaceReferences

interface InputTranslator {
    fun translateFile(fileName: String): SourceSetDeclaration
    fun release()

    fun lower(documentRoot: SourceSetDeclaration): SourceSetModel {

        return documentRoot
                .filterOutNonDeclarations()
                .generateInterfaceReferences()
                .eliminateStringType()
                .desugarArrayDeclarations()
                .introduceNodes()
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
    }
}