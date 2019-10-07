package org.jetbrains.dukat.js.lowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.js.declarations.JSFunctionDeclaration
import org.jetbrains.dukat.js.declarations.JSModuleDeclaration
import org.jetbrains.dukat.js.declarations.JSParameterDeclaration
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME


class JSModuleLowerer(private val moduleDeclaration: JSModuleDeclaration) {

    private val moduleName = moduleDeclaration.name

    private val ANY_NULLABLE_TYPE = TypeValueModel(
            value = IdentifierEntity("Any"),
            params = emptyList<TypeParameterModel>(),
            metaDescription = null,
            nullable = true
    )

    private fun JSParameterDeclaration.convert(): ParameterModel {
        return ParameterModel(
                name = name,
                type = ANY_NULLABLE_TYPE,
                initializer = null,
                vararg = vararg,
                optional = false
        )
    }

    private fun JSFunctionDeclaration.convert(): FunctionModel {
        val parameterModels = mutableListOf<ParameterModel>()

        for(jsParameter in parameters) {
            parameterModels.add(jsParameter.convert())
        }

        val annotations = mutableListOf(AnnotationModel(
                name = "JsModule",
                params = listOf(moduleName)
        ))

        return FunctionModel(
                name = name,
                parameters = parameterModels,
                type = ANY_NULLABLE_TYPE,
                typeParameters = emptyList<TypeParameterModel>(),

                annotations = annotations,

                export = true,
                inline = false,
                operator = false,

                extend = null,
                body = emptyList<StatementModel>()
        )
    }

    fun lower() : SourceSetModel {
        val moduleContents: MutableList<TopLevelModel> = mutableListOf()

        for(functionDeclaration in moduleDeclaration.functions) {
            moduleContents.add(functionDeclaration.convert())
        }

        val sourceFileModel = SourceFileModel(
                name = null,
                fileName = moduleDeclaration.fileName,
                root = ModuleModel(
                        name = ROOT_PACKAGENAME,
                        shortName = ROOT_PACKAGENAME,
                        declarations = moduleContents,
                        annotations = mutableListOf(),
                        submodules = emptyList(),
                        imports = mutableListOf()
                ),
                referencedFiles = emptyList()
        )

        return SourceSetModel("<IRRELEVANT>", listOf(sourceFileModel))
    }

}