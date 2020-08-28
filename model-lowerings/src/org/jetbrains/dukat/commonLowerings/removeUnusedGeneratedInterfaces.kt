package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

class GeneratedInterfaceReferenceCollector : ModelWithOwnerTypeLowering {

    private val usedTypes = mutableSetOf<NameEntity>()

    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val type = super.lowerTypeValueModel(ownerContext)
        if (type.value.isGenerated()) {
            usedTypes += type.value
        }
        return type
    }

    fun isUnused(name: NameEntity): Boolean{
        return !usedTypes.contains(name)
    }
}

class GeneratedInterfaceRemover(private val context: GeneratedInterfaceReferenceCollector) : ModelWithOwnerTypeLowering {
    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        return moduleModel.copy(
            declarations = moduleModel.declarations.filterNot {
                it is InterfaceModel && it.name.isGenerated() && context.isUnused(it.name)
            }
        )
    }
}


class RemoveUnusedGeneratedInterfaces : ModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        val collector = GeneratedInterfaceReferenceCollector()
        collector.lower(source)
        return GeneratedInterfaceRemover(collector).lower(source)
    }
}