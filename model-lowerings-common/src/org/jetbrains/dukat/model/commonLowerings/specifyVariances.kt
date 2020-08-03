package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.Variance
import org.jetbrains.dukat.ownerContext.NodeOwner

private class VarianceContext : ModelWithOwnerTypeLowering {

    private val inPositionTypeParams: MutableSet<NameEntity> = mutableSetOf()
    private val outPositionTypeParams: MutableSet<NameEntity> = mutableSetOf()

    private fun registerInvariantPositionType(type: TypeModel) {
        when (type) {
            is TypeParameterReferenceModel -> {
                inPositionTypeParams += type.name
                outPositionTypeParams += type.name
            }
            is TypeValueModel -> type.params.forEach { registerInvariantPositionType(it) }
            is FunctionTypeModel -> {
                type.parameters.forEach { registerInvariantPositionType(it.type) }
                registerInvariantPositionType(type.type)
            }
            is TypeParameterModel -> registerInvariantPositionType(type.type)
        }
    }

    private fun registerInPositionType(type: TypeModel) {
        when (type) {
            is TypeParameterReferenceModel -> inPositionTypeParams += type.name
            else -> registerInvariantPositionType(type)
        }
    }

    private fun registerOutPositionType(type: TypeModel) {
        when (type) {
            is TypeParameterReferenceModel -> outPositionTypeParams += type.name
            else -> registerInvariantPositionType(type)
        }
    }

    override fun lowerPropertyModel(ownerContext: NodeOwner<PropertyModel>): PropertyModel {
        val declaration = ownerContext.node
        registerOutPositionType(declaration.type)
        if (!declaration.immutable) {
            registerInPositionType(declaration.type)
        }
        return super.lowerPropertyModel(ownerContext)
    }

    override fun lowerMethodModel(ownerContext: NodeOwner<MethodModel>): MethodModel {
        val declaration = ownerContext.node
        declaration.parameters.forEach {
            registerInPositionType(it.type)
        }
        registerOutPositionType(declaration.type)
        return super.lowerMethodModel(ownerContext)
    }

    override fun lowerHeritageModel(ownerContext: NodeOwner<HeritageModel>): HeritageModel {
        ownerContext.node.typeParams.forEach {
            registerInvariantPositionType(it)
        }
        return super.lowerHeritageModel(ownerContext)
    }

    fun resolveVariance(typeParam: TypeParameterModel): Variance {
        val type = typeParam.type
        if (type !is TypeValueModel) {
            return Variance.INVARIANT
        }
        val isInPosition = inPositionTypeParams.contains(type.value)
        val isOutPosition = outPositionTypeParams.contains(type.value)
        return when {
            isInPosition && isOutPosition -> Variance.INVARIANT
            isInPosition -> Variance.CONTRAVARIANT
            isOutPosition -> Variance.COVARIANT
            else -> Variance.INVARIANT
        }
    }
}

private class VarianceLowering : ModelWithOwnerTypeLowering {

    override fun lowerClassLikeModel(
        ownerContext: NodeOwner<ClassLikeModel>,
        parentModule: ModuleModel
    ): ClassLikeModel? {
        val declaration = ownerContext.node
        val context = VarianceContext()
        context.lowerClassLikeModel(ownerContext, parentModule)
        val newTypeParameters = declaration.typeParameters.map {
            it.copy(variance = context.resolveVariance(it))
        }
        return when (declaration) {
            is ClassModel -> declaration.copy(typeParameters = newTypeParameters)
            is InterfaceModel -> declaration.copy(typeParameters = newTypeParameters)
            else -> declaration
        }
    }
}

class SpecifyVariances : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return VarianceLowering().lowerRoot(module, NodeOwner(module, null))
    }
}