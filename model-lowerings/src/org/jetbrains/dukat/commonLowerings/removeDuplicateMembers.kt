package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.model.commonLowerings.ModelContext
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.TopLevelModelLowering
import org.jetbrains.dukat.model.commonLowerings.normalize
import org.jetbrains.dukat.ownerContext.NodeOwner

private class RemoveDuplicateMembersLowering(private val context: ModelContext) : TopLevelModelLowering {

    private fun MemberModel.asKey(): MemberModel {
        return when (this) {
            is MethodModel -> copy(parameters = parameters.map { it.copy(type = it.type.normalize(context)) })
            is ConstructorModel -> copy(parameters = parameters.map { it.copy(type = it.type.normalize(context)) })
            else -> this
        }
    }

    private fun ClassLikeModel.distinctMembers() : List<MemberModel> {
        return members.distinctBy { it.asKey() }
    }

    private fun ObjectModel.distinctMembers() : List<MemberModel> {
        return members.distinctBy { it.asKey() }
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val declaration = ownerContext.node
        return super.lowerClassModel(ownerContext.copy(node = declaration.copy(members = declaration.distinctMembers())), parentModule)
    }

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
        val declaration = ownerContext.node
        return super.lowerInterfaceModel(ownerContext.copy(node = declaration.copy(members = declaration.distinctMembers())), parentModule)
    }

    override fun lowerObjectModel(ownerContext: NodeOwner<ObjectModel>, parentModule: ModuleModel): ObjectModel {
        val declaration = ownerContext.node
        return super.lowerObjectModel(ownerContext.copy(node = declaration.copy(members = declaration.distinctMembers())), parentModule)
    }
}

class RemoveDuplicateMembers : ModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        return source.copy(sources = source.sources.map {
            it.copy(root = RemoveDuplicateMembersLowering(ModelContext(source))
                .lowerRoot(it.root, NodeOwner(it.root, null)))
        })
    }

    override fun lower(module: ModuleModel): ModuleModel {
        return module
    }
}