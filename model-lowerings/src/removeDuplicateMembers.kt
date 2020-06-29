import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.TopLevelModelLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private class RemoveDuplicateMembersLowering : TopLevelModelLowering {

    private fun TypeModel.normalize(): TypeModel {
        return when(this) {
            is TypeValueModel -> copy(metaDescription = null, params = params.map { it.copy(type = it.type.normalize()) })
            is TypeParameterReferenceModel -> copy(metaDescription = null)
            is FunctionTypeModel -> copy(metaDescription = null, parameters = parameters.map { it.copy(type = it.type.normalize()) })
            else -> this
        }
    }

    private fun MemberModel.asKey(): MemberModel {
        return when (this) {
            is MethodModel -> copy(parameters = parameters.map { it.copy(type = it.type.normalize()) })
            is ConstructorModel -> copy(parameters = parameters.map { it.copy(type = it.type.normalize()) })
            else -> this
        }
    }

    private fun ClassLikeModel.distinctMembers() : List<MemberModel> {
        return members.distinctBy { it.asKey() }
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel? {
        val declaration = ownerContext.node
        return super.lowerClassModel(ownerContext.copy(node = declaration.copy(members = declaration.distinctMembers())), parentModule)
    }

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel? {
        val declaration = ownerContext.node
        return super.lowerInterfaceModel(ownerContext.copy(node = declaration.copy(members = declaration.distinctMembers())), parentModule)
    }
}

class RemoveDuplicateMembers : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return RemoveDuplicateMembersLowering().lowerRoot(module, NodeOwner(module, null))
    }
}