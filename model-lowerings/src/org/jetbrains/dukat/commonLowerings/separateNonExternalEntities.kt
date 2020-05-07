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
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.TopLevelModelLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun TopLevelModel.isValidExternalDeclaration(): Boolean {
    return when (this) {
        is FunctionModel -> !inline
        is VariableModel -> !inline
        is TypeAliasModel -> false
        else -> true
    }
}

private class ExternalEntityRegistrator(private val register: (NameEntity, TopLevelModel) -> Unit) : TopLevelModelLowering {

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>, parentModule: ModuleModel): FunctionModel {
        val node = ownerContext.node;
        if (parentModule.canNotContainExternalEntities() && node.inline) {
            register(parentModule.name, node)
        }
        return node
    }

    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>, parentModule: ModuleModel?): VariableModel {
        val node = ownerContext.node;
        if (parentModule != null && parentModule.canNotContainExternalEntities() && node.inline) {
            register(parentModule.name, node)
        }

        return ownerContext.node
    }

    override fun lowerTypeAliasModel(ownerContext: NodeOwner<TypeAliasModel>, parentModule: ModuleModel): TypeAliasModel {
        if (parentModule.canNotContainExternalEntities()) {
            register(parentModule.name, ownerContext.node)
        }

        return ownerContext.node
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

private fun ModuleModel.filterOutExternalDeclarations(): ModuleModel {
    return if (canNotContainExternalEntities()) {
        copy(
                declarations = declarations.filter { it.isValidExternalDeclaration() },
                submodules = submodules.map { it.filterOutExternalDeclarations() }
        )
    } else {
        this.copy(submodules = submodules.map { it.filterOutExternalDeclarations() })
    }
}

class SeparateNonExternalEntities() : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return module.filterOutExternalDeclarations()
    }

    override fun lower(source: SourceSetModel): SourceSetModel {
        val nonDeclarationsBucket = mutableMapOf<NameEntity, MutableList<TopLevelModel>>()
        source.sources.forEach { sourceFile ->
            ExternalEntityRegistrator { name, node ->
                nonDeclarationsBucket.getOrPut(name) { mutableListOf() }.add(node)
            }.lowerRoot(sourceFile.root, NodeOwner(sourceFile.root, null))
        }

        val sourcesLowered = generateDeclarationFiles("nonDeclarations", nonDeclarationsBucket) + source.sources.map { it.copy(root = lower(it.root)) }

        return source.copy(sources = sourcesLowered)
    }
}
