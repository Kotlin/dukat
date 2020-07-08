package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.ownerContext.wrap

interface ModelWithOwnerLowering : TopLevelModelLowering {
    fun lowerFunctionTypeModel(ownerContext: NodeOwner<FunctionTypeModel>): FunctionTypeModel
    fun lowerLambdaParameterModel(ownerContext: NodeOwner<LambdaParameterModel>): LambdaParameterModel
    fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel
    fun lowerMemberModel(ownerContext: NodeOwner<MemberModel>, parentModule: ModuleModel): MemberModel?

    fun lowerTypeParameterModel(ownerContext: NodeOwner<TypeParameterModel>): TypeParameterModel {
        val typeParameterModel = ownerContext.node
        return typeParameterModel.copy(
                type = lowerTypeModel(ownerContext.wrap(typeParameterModel.type)),
                constraints = typeParameterModel.constraints.map { lowerTypeModel(ownerContext.wrap(it)) }
        )
    }

    fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val declaration = ownerContext.node
        return declaration.copy(params = declaration.params.map { param -> lowerTypeParameterModel(ownerContext.wrap(param)) })
    }

    @Suppress("UNCHECKED_CAST")
    fun lowerTypeModel(ownerContext: NodeOwner<TypeModel>): TypeModel {
        return when (val declaration = ownerContext.node) {
            is FunctionTypeModel -> lowerFunctionTypeModel(ownerContext as NodeOwner<FunctionTypeModel>)
            is TypeValueModel -> lowerTypeValueModel(ownerContext as NodeOwner<TypeValueModel>)
            else -> declaration
        }
    }

    fun lowerStatementBody(ownerContext: NodeOwner<BlockStatementModel>) : BlockStatementModel {
        val declaration = ownerContext.node
        return declaration.copy(statements = declaration.statements.map { statementModel ->
            lowerStatementModel(ownerContext.wrap(statementModel))
        })
    }

    fun lowerStatementModel(ownerContext: NodeOwner<StatementModel>) : StatementModel {
        return ownerContext.node
    }
}
