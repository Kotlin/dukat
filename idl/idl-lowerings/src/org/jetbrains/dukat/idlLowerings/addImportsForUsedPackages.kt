package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astModel.ImportModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.stdlib.isKotlinStdlibPrefixed
import org.jetbrains.dukat.stdlib.isTsStdlibPrefixed
import org.jetbrains.dukat.translatorString.translate

private class ImportContext(sourceSetModel: SourceSetModel) : ModelWithOwnerTypeLowering {
    private val packageContainingClass: MutableMap<NameEntity, NameEntity> = mutableMapOf()

    override fun lowerTopLevelModel(ownerContext: NodeOwner<TopLevelModel>, parentModule: ModuleModel): TopLevelModel {
        packageContainingClass[ownerContext.node.name] = parentModule.name

        return super.lowerTopLevelModel(ownerContext, parentModule)
    }

    fun getPackageNameByClassName(className: NameEntity): NameEntity? {
        return packageContainingClass[className]
    }

    init {
        sourceSetModel.sources.forEach { lowerRoot(it.root, NodeOwner(it.root, null)) }
    }
}

private class AddImportsLowering(
    private val sourceFileModel: SourceFileModel,
    private val importContext: ImportContext
) : ModelWithOwnerTypeLowering {
    private val usedPackages: MutableSet<NameEntity> = mutableSetOf()

    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        importContext.getPackageNameByClassName(ownerContext.node.value)?.let { usedPackages += it }
        return super.lowerTypeValueModel(ownerContext)
    }

    fun getNewImports(): List<ImportModel> {
        return ((sourceFileModel.root.imports + usedPackages.map {
            ImportModel(
                if (it.isKotlinStdlibPrefixed()) {
                    it.shiftLeft()
                } else {
                    it
                }!!.appendLeft(IdentifierEntity("*"))
            )
        }).distinct() - ImportModel(sourceFileModel.root.name.appendLeft(IdentifierEntity("*"))))
            .sortedBy { it.name.translate() }
    }

    init {
        lowerRoot(sourceFileModel.root, NodeOwner(sourceFileModel.root, null))
    }
}

fun SourceSetModel.addImportsForUsedPackages(): SourceSetModel {
    val context = ImportContext(this)
    return copy(
        sources = sources.map {
            it.copy(
                root = it.root.copy(
                    imports = AddImportsLowering(it, context).getNewImports().toMutableList()
                )
            )
        }
    )
}
