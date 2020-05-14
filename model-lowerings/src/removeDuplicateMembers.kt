import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.TopLevelModelLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private class RemoveDuplicateMembersLowering : TopLevelModelLowering {

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel? {
        val declaration = ownerContext.node
        return super.lowerClassModel(ownerContext.copy(node = declaration.copy(members = declaration.members.distinct())), parentModule)
    }

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel? {
        val declaration = ownerContext.node
        return super.lowerInterfaceModel(ownerContext.copy(node = declaration.copy(members = declaration.members.distinct())), parentModule)
    }
}

class RemoveDuplicateMembers : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return RemoveDuplicateMembersLowering().lowerRoot(module, NodeOwner(module, null))
    }
}