package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.ClassLikeReferenceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.stdlib.TSLIBROOT


private fun starParam() = TypeParameterModel(
        TypeValueModel(IdentifierEntity("*"), emptyList(), null, null, false),
        emptyList()
)

private fun ClassLikeReferenceModel.correct(): ClassLikeReferenceModel {
    return if (name == IdentifierEntity("Function")) {
        copy(typeParameters = listOf(IdentifierEntity("*")))
    } else {
        this
    }
}


private class StdLibTypeCorrectorLowering : ModelWithOwnerTypeLowering {
    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>, parentModule: ModuleModel): VariableModel {
        val node = ownerContext.node
        val nodeResolved = node.copy(extend = node.extend?.correct())
        return super.lowerVariableModel(ownerContext.copy(node = nodeResolved), parentModule)
    }

    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val node = ownerContext.node
        val nodeResolved = if (QualifierEntity(TSLIBROOT, IdentifierEntity("Function")) == node.fqName) {
            node.copy(params = listOf(starParam()))
        } else {
            node
        }
        return super.lowerTypeValueModel(ownerContext.copy(node = nodeResolved))
    }
}

class CorrectStdLibTypes : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return StdLibTypeCorrectorLowering().lowerRoot(module, NodeOwner(module, null))
    }
}