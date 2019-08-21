package org.jetbrains.dukat.translator

import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
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

    fun visitObject(objectNode: ObjectModel)
    private fun processObjectModel(declaration: ObjectModel) {
        visitObject(declaration)
    }

    fun visitEnum(enumNode: EnumModel)
    private fun processEnumNode(declaration: EnumModel) {
        visitEnum(declaration)
    }

    fun visitTypeAlias(typeAlias: TypeAliasModel)
    private fun processTypeAliasModel(declaration: TypeAliasModel) {
        visitTypeAlias(declaration)
    }

    fun visitInterface(interfaceModel: InterfaceModel)
    private fun processInterfaceModel(declaration: InterfaceModel) {
        visitInterface(declaration)
    }

    fun visitClass(classModel: ClassModel)
    private fun processClassModel(declaration: ClassModel) {
        visitClass(declaration)
    }

    private fun processClassLikeModel(declaration: ClassLikeModel) {
        when (declaration) {
            is InterfaceModel -> processInterfaceModel(declaration)
            is ClassModel -> processClassModel(declaration)
        }
    }

    private fun processTopLevelDeclaration(declaration: TopLevelModel) {
        when (declaration) {
            is VariableModel -> processVariableModel(declaration)
            is FunctionModel -> processFunctionModel(declaration)
            is ClassLikeModel -> processClassLikeModel(declaration)
            is ObjectModel -> processObjectModel(declaration)
            is EnumModel -> processEnumNode(declaration)
            is TypeAliasModel -> processTypeAliasModel(declaration)
            else -> raiseConcern("unable to process TopLevelDeclaration ${declaration}") {  }
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
