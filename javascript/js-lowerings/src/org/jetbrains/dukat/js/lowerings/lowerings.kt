package org.jetbrains.dukat.js.lowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.js.declarations.JSFunctionDeclaration
import org.jetbrains.dukat.js.declarations.JSModuleDeclaration

private val ANY_NULLABLE_TYPE = TypeValueModel(
        IdentifierEntity("Any"),
        emptyList<TypeParameterModel>(),
        null,
        true
)

fun JSFunctionDeclaration.convert(): FunctionModel {
    val parameterModels = mutableListOf<ParameterModel>()

    for(parameterName in parameters) {
        parameterModels.add(ParameterModel(
                name = parameterName,
                type = ANY_NULLABLE_TYPE,
                initializer = null,
                vararg = false,
                optional = false
        ))
    }

    return FunctionModel(
            name = name,
            parameters = parameterModels,
            type = ANY_NULLABLE_TYPE,
            typeParameters = emptyList<TypeParameterModel>(),

            annotations = mutableListOf<AnnotationModel>(),

            export = true,
            inline = false,
            operator = false,

            extend = null,
            body = emptyList<StatementModel>()
    )
}


fun JSModuleDeclaration.convert(): SourceSetModel {
    val moduleContents: MutableList<TopLevelModel> = mutableListOf()

    for(functionDeclaration in functions) {
        moduleContents.add(functionDeclaration.convert())
    }

    val sourceFileModel = SourceFileModel(
            name = name,
            fileName = fileName,
            root = ModuleModel(
                    name = name,
                    shortName = name,
                    declarations = moduleContents,
                    annotations = mutableListOf(),
                    submodules = emptyList(),
                    imports = mutableListOf()
            ),
            referencedFiles = emptyList()
    )

    return SourceSetModel(fileName, listOf(sourceFileModel))
}
