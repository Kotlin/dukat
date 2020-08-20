package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.model.commonLowerings.TopLevelModelLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private class ChangeFqNameLowering(private val fqNameMap: Map<NameEntity, NameEntity>) : ModelWithOwnerTypeLowering {
    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val node = ownerContext.node
        val nodeResolved = fqNameMap[node.fqName]?.let {
            node.copy(fqName = it)
        }
        return super.lowerTypeValueModel(ownerContext.copy(node = nodeResolved ?: node))
    }
}

class ExtractNestedInheritedInterfaces : TopLevelModelLowering {

    private val extractedMap = mutableMapOf<TopLevelModel, List<InterfaceModel>>()
    private val fqNameMap = mutableMapOf<NameEntity, NameEntity>()

    @Suppress("UNCHECKED_CAST")
    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val node = ownerContext.node
        val parentNames = node.parentEntities.mapNotNull {
            it.value.fqName
        }.toSet()

        val classFqName = parentModule.name.appendLeft(node.name)
        val (nestedInheritedInterfaces, validMembers) = node.members.partition {
            if (it is InterfaceModel) {
                val fqName= classFqName.appendLeft(it.name)
                parentNames.contains(fqName)
            } else {
                false
            }
        }

        val classModel = node.copy(members = validMembers)
        extractedMap[classModel] = (nestedInheritedInterfaces as List<InterfaceModel>).map { it.copy(external = true) }

        return classModel
    }


    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        val module =  super.lowerRoot(moduleModel, ownerContext)

        val declarationsResolved = module.declarations.flatMap { topLevelModel ->
            val name = moduleModel.name.appendLeft(topLevelModel.name)
            listOf(topLevelModel) + (extractedMap[topLevelModel]?.also { interfaceModels ->
                interfaceModels.forEach { interfaceModel ->
                    fqNameMap[name.appendLeft(interfaceModel.name)] = ownerContext.node.name.appendLeft(interfaceModel.name)
                }
            } ?: emptyList())
        }

        return module.copy(declarations = declarationsResolved)
    }

    override fun lower(source: SourceSetModel): SourceSetModel {
        val sourceSet = super.lower(source)
        return ChangeFqNameLowering(fqNameMap).lower(sourceSet)
    }
}