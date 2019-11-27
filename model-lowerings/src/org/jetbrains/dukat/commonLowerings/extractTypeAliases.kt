package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private class TypeAliasRegistrator(private val register: (NameEntity, TypeAliasModel) -> Unit) : ModelWithOwnerTypeLowering {

    override fun lowerTypeAliasModel(ownerContext: NodeOwner<TypeAliasModel>, moduleOwner: ModuleModel): TypeAliasModel {
        if (moduleOwner.canNotContainTypeAliases()) {
            register(moduleOwner.name, ownerContext.node)
        }
        return super.lowerTypeAliasModel(ownerContext, moduleOwner)
    }
}

private fun getAliasFiles(aliasBucket: Map<Pair<NameEntity, String>, List<TypeAliasModel>>): List<SourceFileModel> {
    return aliasBucket.map { (nameData, aliases) ->
        val (packageName, fileName) = nameData
        SourceFileModel(
                fileName = fileName,
                root = ModuleModel(
                        name = packageName,
                        shortName = packageName.rightMost(),
                        declarations = aliases,
                        annotations = mutableListOf(),
                        submodules = emptyList(),
                        imports = mutableListOf(),
                        comment = null
                ),
                name = packageName,
                referencedFiles = emptyList()
        )

    }
}

private fun ModuleModel.canNotContainTypeAliases(): Boolean {
    return annotations.any {
        (it.name == "file:JsQualifier") || (it.name == "file:JsModule")
    }
}

private fun ModuleModel.filterOutTypeAliases(): ModuleModel {
    return if (canNotContainTypeAliases()) {
        copy(
                declarations = declarations.filterNot { it is TypeAliasModel },
                submodules = submodules.map { it.filterOutTypeAliases() }
        )
    } else {
        this.copy(submodules = submodules.map { it.filterOutTypeAliases() })
    }
}

fun SourceSetModel.extractTypeAliases(): SourceSetModel {
    val aliasBucket = mutableMapOf<Pair<NameEntity, String>, MutableList<TypeAliasModel>>()
    sources.forEach { source ->
        TypeAliasRegistrator { name, node ->
            aliasBucket.getOrPut(Pair(name, source.fileName)) { mutableListOf() }.add(node)
        }.lowerRoot(source.root, NodeOwner(source.root, null))
    }

    val sourcesLowered =
            getAliasFiles(aliasBucket) + sources.map { source -> source.copy(root = source.root.filterOutTypeAliases()) }

    return copy(sources = sourcesLowered)
}