package org.jetbrains.dukat.js.lowerings

import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.js.declarations.toplevel.JSFunctionDeclaration
import org.jetbrains.dukat.js.declarations.JSModuleDeclaration
import org.jetbrains.dukat.js.declarations.misc.JSParameterDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSClassDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSReferenceDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSDeclaration
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME


class JSModuleFileLowerer(private val moduleDeclaration: JSModuleDeclaration) {

    private val logger = Logging.logger("Lowering")

    private val moduleName = IdentifierEntity(moduleDeclaration.moduleName)

    private val MODULE_ANNOTATION = AnnotationModel(
            name = "JsModule",
            params = listOf(moduleName)
    )

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

        return FunctionModel(
                name = IdentifierEntity(name),
                parameters = parameterModels,
                type = ANY_NULLABLE_TYPE,
                typeParameters = emptyList<TypeParameterModel>(),

                annotations = mutableListOf(MODULE_ANNOTATION),

                export = true,
                inline = false,
                operator = false,

                extend = null,
                body = emptyList<StatementModel>()
        )
    }

    private fun JSClassDeclaration.convert(): ClassModel {
        return ClassModel(
                name = IdentifierEntity(name),
                members = listOf<MemberModel>(),
                companionObject = null,
                typeParameters = emptyList(),
                parentEntities = emptyList(),
                primaryConstructor = null,
                annotations = mutableListOf(MODULE_ANNOTATION),
                comment = null,
                external = true,
                abstract = false
        )
    }

    private fun JSDeclaration.convert(): TopLevelModel {
        return when(this) {
            is JSFunctionDeclaration -> this.convert()
            is JSClassDeclaration -> this.convert()
            is JSReferenceDeclaration -> throw IllegalStateException("References need to be resolved before conversion to model.")
            else -> throw IllegalStateException("Declaration of type <${this.javaClass}> cannot be converted.")
        }
    }

    private fun resolve(reference: JSReferenceDeclaration): JSDeclaration? {
        return moduleDeclaration.topLevelDeclarations[reference.name]
    }

    private fun resolveReferencedExportDeclarations() {
        for(declaration in moduleDeclaration.exportDeclarations) {
            if(declaration is JSReferenceDeclaration) {
                moduleDeclaration.exportDeclarations.remove(declaration)

                var resolvedDeclaration: JSDeclaration? = declaration

                while(resolvedDeclaration is JSReferenceDeclaration) {
                    resolvedDeclaration = resolve(resolvedDeclaration)
                }

                if(resolvedDeclaration != null) {
                    moduleDeclaration.exportDeclarations.add(resolvedDeclaration)
                } else {
                    logger.warn("Cannot resolve reference for export: " + declaration.name)
                }
            }
        }
    }

    fun lower() : SourceSetModel {
        resolveReferencedExportDeclarations()

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
