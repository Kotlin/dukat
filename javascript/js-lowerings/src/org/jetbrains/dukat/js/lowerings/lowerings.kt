package org.jetbrains.dukat.js.lowerings

import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.js.declarations.general.JSFunctionDeclaration
import org.jetbrains.dukat.js.declarations.JSModuleDeclaration
import org.jetbrains.dukat.js.declarations.misc.JSParameterDeclaration
import org.jetbrains.dukat.js.declarations.general.JSClassDeclaration
import org.jetbrains.dukat.js.declarations.JSDeclaration
import org.jetbrains.dukat.js.declarations.general.JSObjectDeclaration
import org.jetbrains.dukat.js.declarations.member.JSMethodDeclaration
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME


class JSModuleFileLowerer(private val moduleDeclaration: JSModuleDeclaration) {

    private val ANY_NULLABLE_TYPE = TypeValueModel(
            value = IdentifierEntity("Any"),
            params = emptyList<TypeParameterModel>(),
            metaDescription = null,
            nullable = true
    )

    private val logger = Logging.logger("Lowering")

    private val moduleName = IdentifierEntity(moduleDeclaration.moduleName)

    private val moduleAnnotation = AnnotationModel(
            name = "JsModule",
            params = listOf(moduleName)
    )

    private val moduleFileAnnotation = AnnotationModel(
            name = "file:JsModule",
            params = listOf(moduleName)
    )


    private var useFileLevelAnnotation = false


    private fun getIndividualAnnotations() = if (!useFileLevelAnnotation) mutableListOf(moduleAnnotation) else mutableListOf()
    private fun getFileAnnotations() = if (useFileLevelAnnotation) mutableListOf(moduleFileAnnotation) else mutableListOf()


    private fun JSParameterDeclaration.convert(): ParameterModel {
        return ParameterModel(
                name = name,
                type = ANY_NULLABLE_TYPE,
                initializer = null,
                vararg = vararg,
                optional = false
        )
    }

    private fun List<JSParameterDeclaration>.convert(): List<ParameterModel> {
        return this.map { it.convert() }
    }

    private fun JSFunctionDeclaration.convertAs(name: String): FunctionModel {
        return FunctionModel(
                name = IdentifierEntity(name),
                parameters = parameters.convert(),
                type = ANY_NULLABLE_TYPE,
                typeParameters = emptyList<TypeParameterModel>(),

                annotations = getIndividualAnnotations(),

                export = true,
                inline = false,
                operator = false,

                extend = null,
                body = emptyList<StatementModel>(),
                visibilityModifier = VisibilityModifierModel.DEFAULT
        )
    }

    private fun JSMethodDeclaration.convertAs(name: String): MethodModel {
        return MethodModel(
                name = IdentifierEntity(name),
                parameters = function.parameters.convert(),
                type = ANY_NULLABLE_TYPE,
                typeParameters = emptyList<TypeParameterModel>(),

                static = static,
                override = false,
                operator = false,
                annotations = emptyList(),

                open = false
        )
    }

    private fun JSClassDeclaration.convertAs(name: String): ClassModel {
        val members = mutableListOf<MemberModel>()

        for ((memberName, declaration) in scopeDeclarations) {
            when (declaration) {
                is JSMethodDeclaration -> members.add(declaration.convertAs(memberName))
                else -> logger.warn("Class member of type <${declaration.javaClass}> not supported.")
            }
        }

        return ClassModel(
                name = IdentifierEntity(name),
                members = members,
                companionObject = null,
                typeParameters = emptyList(),
                parentEntities = emptyList(),
                primaryConstructor = null,
                annotations = getIndividualAnnotations(),
                comment = null,
                external = true,
                abstract = false,
                visibilityModifier = VisibilityModifierModel.DEFAULT
        )
    }

    private fun JSDeclaration.convertAs(name: String): TopLevelModel {
        return when (this) {
            is JSFunctionDeclaration -> this.convertAs(name)
            is JSClassDeclaration -> this.convertAs(name)
            is JSMethodDeclaration -> this.function.convertAs(name)
            else -> throw IllegalStateException("Declaration of type <${this.javaClass}> cannot be converted.")
        }
    }

    private fun JSDeclaration.convert(): TopLevelModel {
        return when (this) {
            is JSFunctionDeclaration -> this.convertAs(this.name)
            is JSClassDeclaration -> this.convertAs(this.name)
            else -> throw IllegalStateException("Declaration of type <${this.javaClass}> cannot be converted.")
        }
    }

    private fun JSObjectDeclaration.toModuleContents() : List<TopLevelModel> {
        useFileLevelAnnotation = true

        return scopeDeclarations.map { (name, declaration) -> declaration.convertAs(name) }
    }


    private fun JSDeclaration?.toModuleContents(): List<TopLevelModel> {
        return when (this) {
            null -> emptyList()
            is JSObjectDeclaration -> this.toModuleContents()
            else -> listOf(this.convert())
        }
    }


    fun lower() : SourceSetModel {
        val moduleContents = moduleDeclaration.exportDeclaration.toModuleContents()

        val sourceFileModel = SourceFileModel(
                name = null,
                fileName = moduleDeclaration.fileName,
                root = ModuleModel(
                        name = ROOT_PACKAGENAME,
                        shortName = ROOT_PACKAGENAME,
                        declarations = moduleContents,
                        annotations = getFileAnnotations(),
                        submodules = emptyList(),
                        imports = mutableListOf()
                ),
                referencedFiles = emptyList()
        )

        return SourceSetModel("<IRRELEVANT>", listOf(sourceFileModel))
    }

}
