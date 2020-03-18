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
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun TopLevelModel.isValidExternalDeclaration(): Boolean {
    return when (this) {
        is FunctionModel -> !inline
        is VariableModel -> !inline
        is TypeAliasModel -> false
        else -> true
    }
}

private class ExternalEntityRegistrator(private val register: (NameEntity, TopLevelModel) -> Unit) : ModelWithOwnerTypeLowering {

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>, parentModule: ModuleModel): FunctionModel {
        val node = ownerContext.node;
        if (parentModule.canNotContainExternalEntities() && node.inline) {
            register(parentModule.name, node)
        }
        return super.lowerFunctionModel(ownerContext, parentModule)
    }

    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>, parentModule: ModuleModel): VariableModel {
        val node = ownerContext.node;
        if (parentModule.canNotContainExternalEntities() && node.inline) {
            register(parentModule.name, node)
        }
        return super.lowerVariableModel(ownerContext, parentModule)
    }

    override fun lowerTypeAliasModel(ownerContext: NodeOwner<TypeAliasModel>, parentModule: ModuleModel): TypeAliasModel {
        if (parentModule.canNotContainExternalEntities()) {
            register(parentModule.name, ownerContext.node)
        }
        return super.lowerTypeAliasModel(ownerContext, parentModule)
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

private fun SourceSetModel.extractNonExternalDeclarations(): SourceSetModel {
    val nonDeclarationsBucket = mutableMapOf<NameEntity, MutableList<TopLevelModel>>()
    sources.forEach { source ->
        ExternalEntityRegistrator { name, node ->
            nonDeclarationsBucket.getOrPut(name) { mutableListOf() }.add(node)
        }.lowerRoot(source.root, NodeOwner(source.root, null))
    }

    val sourcesLowered = generateDeclarationFiles("nonDeclarations", nonDeclarationsBucket) + sources.map { source -> source.copy(root = source.root.filterOutExternalDeclarations()) }

    return copy(sources = sourcesLowered)
}

class ExtractNonExternalDeclarations() : ModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        return source.extractNonExternalDeclarations()
    }
}
