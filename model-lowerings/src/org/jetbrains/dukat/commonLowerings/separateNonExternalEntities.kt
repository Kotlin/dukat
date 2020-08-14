package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.model.commonLowerings.TopLevelModelLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

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

class SeparateNonExternalEntities : TopLevelModelLowering {
    private val nonDeclarationsMap = mutableMapOf<NameEntity, MutableList<TopLevelModel>>()

    private fun ModuleModel.extractNonDeclarations(): ModuleModel {
        return if (canNotContainExternalEntities()) {
            copy(declarations = declarations.mapNotNull { declaration ->
                when(declaration) {
                    is ModuleModel -> extractNonDeclarations()
                    else -> {
                        if (declaration.isValidExternalDeclaration()) {
                            declaration
                        } else {
                            nonDeclarationsMap.getOrPut(name) { mutableListOf() }.add(declaration)
                            null
                        }
                    }
                }
            })
        } else {
            copy(declarations = declarations.map { declaration ->
                when(declaration) {
                    is ModuleModel -> extractNonDeclarations()
                    else -> declaration
                }
            })
        }
    }


    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        return super.lowerRoot(moduleModel.extractNonDeclarations(), ownerContext)
    }


    override fun lower(source: SourceSetModel): SourceSetModel {
        val sourceSet = super.lower(source)
        val nonDeclarationFiles = generateDeclarationFiles("nonDeclarations", nonDeclarationsMap )

        return sourceSet.copy(sources = (nonDeclarationFiles + sourceSet.sources))
    }
}
