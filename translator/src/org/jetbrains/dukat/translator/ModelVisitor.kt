package org.jetbrains.dukat.translator

import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.panic.raiseConcern

interface ModelVisitor {

    fun visitVariable(variable: VariableModel)
    private fun processVariableModel(declaration: VariableModel) {
        visitVariable(declaration)
    }


    fun visitFunction(function: FunctionModel)
    private fun processFunctionModel(declaration: FunctionModel) {
        visitFunction(declaration)
    }

    private fun processParameterModel(ownerContext: ParameterModel) {}
    private fun processMemberNode(declaration: MemberNode) {}

    fun visitObject(objectNode: ObjectModel)
    private fun processObjectModel(declaration: ObjectModel) {
        visitObject(declaration)
    }

    fun visitEnum(enumNode: EnumNode)
    private fun processEnumNode(declaration: EnumNode) {
        visitEnum(declaration)
    }

    fun visitTypeAlias(typeAlias: TypeAliasModel)
    private fun processTypeAliasModel(declaration: TypeAliasModel) {
        visitTypeAlias(declaration)
    }

    fun visitInterface(interfaceModel: InterfaceModel)
    private fun processInterfaceModel(declaration: InterfaceModel) {
        val declaration = declaration

        visitInterface(declaration)

        declaration.members.forEach { member ->
            processMemberNode(member)
        }

        declaration.parentEntities.forEach { heritageClause ->
            processHeritageNode(heritageClause)
        }
    }


    fun visitClass(classModel: ClassModel)
    private fun processClassModel(declaration: ClassModel) {
        visitClass(declaration)
        val declaration = declaration

        declaration.members.forEach { member ->
            processMemberNode(member)
        }

        declaration.parentEntities.forEach { heritageClause ->
            processHeritageNode(heritageClause)
        }
    }

    private fun processHeritageNode(declaration: HeritageModel) {
        val heritageClause = declaration
    }


    private fun processClassLikeModel(declaration: ClassLikeModel) {
        val declaration = declaration
        when (declaration) {
            is InterfaceModel -> processInterfaceModel(declaration)
            is ClassModel -> processClassModel(declaration)
        }
    }

    private fun processTopLevelDeclaration(declaration: TopLevelNode) {
        val declaration = declaration
        when (declaration) {
            is VariableModel -> processVariableModel(declaration)
            is FunctionModel -> processFunctionModel(declaration)
            is ClassLikeModel -> processClassLikeModel(declaration)
            is ObjectModel -> processObjectModel(declaration)
            is EnumNode -> processEnumNode(declaration)
            is TypeAliasModel -> processTypeAliasModel(declaration)
            else -> raiseConcern("unable to process TopeLevelDeclaration ${declaration::class.simpleName}") {  }
        }
    }


    fun visitModule(moduleModel: ModuleModel)

    fun process(moduleModel: ModuleModel) {

        visitModule(moduleModel)

        moduleModel.declarations.forEach { declaration ->
            processTopLevelDeclaration(declaration)
        }

    }
}
