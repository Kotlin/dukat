package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.Lowering
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.VariableModel

private fun TopLevelModel.isValidExternalDeclaration(): Boolean {
    return when (this) {
        is FunctionModel -> external
        is VariableModel -> external
        is TypeAliasModel -> false
        else -> true
    }
}

private fun generateDeclarationFiles(id: String, declarationsBucket: Map<NameEntity, List<TopLevelModel>>): List<SourceFileModel> {
    return declarationsBucket.map { (packageName, aliases) ->
        SourceFileModel(
                fileName = "nonDeclarations",
                root = ModuleModel(
                        name = packageName,
                        shortName = packageName.rightMost(),
                        declarations = aliases,
                        annotations = mutableListOf(),
                        submodules = emptyList(),
                        imports = mutableListOf(),
                        comment = null
                ),
                name = IdentifierEntity(id),
                referencedFiles = emptyList()
        )

    }
}


private fun ModuleModel.canNotContainExternalEntities(): Boolean {
    return annotations.any {
        (it.name == "file:JsQualifier") || (it.name == "file:JsModule")
    }
}

class SeparateNonExternalEntities : Lowering<SourceSetModel, SourceSetModel> {

    private val separatedModules = mutableMapOf<NameEntity, MutableList<ModuleModel>>()

    private fun lower(moduleModel: ModuleModel): ModuleModel {
        val canNotContainExternalEntities = moduleModel.canNotContainExternalEntities()
        val (validDeclarations, separatedDeclarations) = moduleModel.declarations.partition {
            if (canNotContainExternalEntities) {
                it.isValidExternalDeclaration()
            } else {
                true
            }
        }

        if (separatedDeclarations.isNotEmpty()) {
            separatedModules
                    .getOrPut(moduleModel.name) { mutableListOf() }
                    .add(moduleModel.copy(
                            declarations = separatedDeclarations,
                            annotations = mutableListOf(),
                            submodules = emptyList()
                    ))
        }

        return moduleModel.copy(declarations = validDeclarations, submodules = moduleModel.submodules.map { lower(it) })
    }

    private fun lower(sourceFile: SourceFileModel): SourceFileModel {
        return sourceFile.copy(
                root = lower(sourceFile.root)
        )
    }

    override fun lower(source: SourceSetModel): SourceSetModel {
        val sourceSet = source.copy(sources = source.sources.map { sourceFile -> lower(sourceFile) })

        val generatedFiles = separatedModules.flatMap { (_, modules) ->
            modules.map { module ->
                SourceFileModel(
                    fileName = "nonDeclarations",
                    root = module,
                    name = null,
                    referencedFiles = emptyList()
                )
            }
        }

        return sourceSet.copy(sources = generatedFiles + sourceSet.sources)
    }
}
