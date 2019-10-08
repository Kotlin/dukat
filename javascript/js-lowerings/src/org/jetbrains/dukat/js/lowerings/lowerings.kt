package org.jetbrains.dukat.js.lowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.js.declarations.toplevel.JSFunctionDeclaration
import org.jetbrains.dukat.js.declarations.JSModuleDeclaration
import org.jetbrains.dukat.js.declarations.export.JSExportDeclaration
import org.jetbrains.dukat.js.declarations.export.JSInlineExportDeclaration
import org.jetbrains.dukat.js.declarations.misc.JSParameterDeclaration
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME


class JSModuleFileLowerer(private val moduleDeclaration: JSModuleDeclaration) {

    private val moduleName = IdentifierEntity(moduleDeclaration.moduleName)

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
                name = IdentifierEntity(name),
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

    private fun JSInlineExportDeclaration.convert(): TopLevelModel {
        return when(this.declaration) {
            is JSFunctionDeclaration -> (this.declaration as JSFunctionDeclaration).convert()
            else -> throw IllegalStateException("Export declaration with declaration of type <${this.javaClass}> not supported!")
        }
    }

    private fun JSExportDeclaration.convert(): TopLevelModel {
        return when(this) {
            is JSInlineExportDeclaration -> this.convert()
            else -> throw IllegalStateException("Export declaration of type <${this.javaClass}> not supported!")
        }
    }

    fun lower() : SourceSetModel {
        val moduleContents: MutableList<TopLevelModel> = mutableListOf()

        for(exportDeclaration in moduleDeclaration.exportDeclarations) {
            moduleContents.add(exportDeclaration.convert())
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