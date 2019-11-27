package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private class TypeAliasRegistrator : ModelWithOwnerTypeLowering {
    val aliasBucket = mutableMapOf<NameEntity, MutableList<TypeAliasModel>>()

    override fun lowerTypeAliasModel(ownerContext: NodeOwner<TypeAliasModel>, moduleOwner: ModuleModel): TypeAliasModel {
        if (moduleOwner.canNotContainTypeAliases()) {
            val node = ownerContext.node
            aliasBucket.getOrPut(moduleOwner.name) { mutableListOf() }.add(node)
        }
        return super.lowerTypeAliasModel(ownerContext, moduleOwner)
    }

    fun getAliasFiles(): List<SourceFileModel> {
        return aliasBucket.map { (name, aliases) ->
            val fileName = name.rightMost().value
            val packageName = name.shiftLeft()!!
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
    val typeAliasRegistrator = TypeAliasRegistrator()
    sources.forEach { source ->
        val sourceWithFileName = source.copy(root = source.root.copy(name = source.root.name.appendLeft(IdentifierEntity(source.fileName))))
        typeAliasRegistrator.lowerRoot(sourceWithFileName.root, NodeOwner(sourceWithFileName.root, null))
    }

    val sourcesLowered =
            typeAliasRegistrator.getAliasFiles() + sources.map { source -> source.copy(root = source.root.filterOutTypeAliases()) }

    return copy(sources = sourcesLowered)
}