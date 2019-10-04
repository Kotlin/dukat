package org.jetbrains.dukat.jsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.jsDeclarations.JSModuleDeclaration

fun JSModuleDeclaration.convert(): SourceSetModel {
    val moduleDeclarations: MutableList<TopLevelModel> = mutableListOf()

    moduleDeclarations.add(FunctionModel(
            name = IdentifierEntity("myFirstGeneratedFun"),
            parameters = emptyList<ParameterModel>(),
            type = TypeValueModel(
                    IdentifierEntity("Unit"),
                    emptyList<TypeParameterModel>(),
                    null,
                    false
            ),
            typeParameters = emptyList<TypeParameterModel>(),

            annotations = mutableListOf<AnnotationModel>(),

            export = true,
            inline = false,
            operator = false,

            extend = null,
            body = emptyList<StatementModel>()
    ))


    val sourceFileModel = SourceFileModel(
            name = this.name,
            fileName = this.fileName,
            root = ModuleModel(
                    name = this.name,
                    shortName = this.name,
                    declarations = moduleDeclarations,
                    annotations = mutableListOf(),
                    submodules = emptyList(),
                    imports = mutableListOf()
            ),
            referencedFiles = emptyList()
    )

    return SourceSetModel(this.fileName, listOf(sourceFileModel))
}
