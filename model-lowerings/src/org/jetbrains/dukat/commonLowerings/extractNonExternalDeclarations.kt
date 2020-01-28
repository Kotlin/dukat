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
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private class ExternalEntityRegistrator(private val register: (NameEntity, TopLevelModel) -> Unit) : ModelWithOwnerTypeLowering {

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>, parentModule: ModuleModel): FunctionModel {
        val node = ownerContext.node;
        if (parentModule.canNotContainExternalEntities() && node.inline) {
            register(parentModule.name, node)
        }
        return super.lowerFunctionModel(ownerContext, parentModule)
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
                declarations = declarations.filterNot { (it is TypeAliasModel) || ((it is FunctionModel) && it.inline) },
                submodules = submodules.map { it.filterOutExternalDeclarations() }
        )
    } else {
        this.copy(submodules = submodules.map { it.filterOutExternalDeclarations() })
    }
}

fun SourceSetModel.extractNonExternalDeclarations(): SourceSetModel {
    val aliasBucket = mutableMapOf<NameEntity, MutableList<TypeAliasModel>>()
    val functionsBucket = mutableMapOf<NameEntity, MutableList<FunctionModel>>()
    sources.forEach { source ->
        ExternalEntityRegistrator { name, node ->
            when (node) {
                is TypeAliasModel -> aliasBucket.getOrPut(name) { mutableListOf() }.add(node)
                is FunctionModel -> if (node.inline) {
                    functionsBucket.getOrPut(name) { mutableListOf() }.add(node)
                }
            }
        }.lowerRoot(source.root, NodeOwner(source.root, null))
    }

    val sourcesLowered =
            generateDeclarationFiles("aliases", aliasBucket) + generateDeclarationFiles("inlined", functionsBucket) + sources.map { source -> source.copy(root = source.root.filterOutExternalDeclarations()) }

    return copy(sources = sourcesLowered)
}