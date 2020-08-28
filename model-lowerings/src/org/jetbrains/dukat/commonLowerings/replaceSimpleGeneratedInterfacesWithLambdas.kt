package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

internal fun NameEntity.isGeneratedLambda(): Boolean {
    return rightMost().value.startsWith("`L\$")
}

internal fun NameEntity.isGenerated(): Boolean {
    return isGeneratedLambda() || rightMost().value.startsWith("`T\$")
}

private fun InterfaceModel.isSimpleGeneratedLambda(): Boolean {
    return name.isGeneratedLambda() && typeParameters.isEmpty() && members.size == 1 && members[0] is MethodModel
}

private class GeneratedInterfaceCollector : ModelWithOwnerTypeLowering {

    private val lambdas = mutableMapOf<NameEntity, FunctionTypeModel>()

    override fun lowerInterfaceModel(
        ownerContext: NodeOwner<InterfaceModel>,
        parentModule: ModuleModel
    ): InterfaceModel {

        val declaration = super.lowerInterfaceModel(ownerContext, parentModule)

        if (declaration.isSimpleGeneratedLambda()) {
            val singleMethod = declaration.members[0] as MethodModel
            lambdas[declaration.name] = FunctionTypeModel(
                parameters = singleMethod.parameters.map {
                    LambdaParameterModel(
                        name = it.name,
                        type = it.type,
                        explicitlyDeclaredType = true
                    )
                },
                type = singleMethod.type,
                metaDescription = null
            )
        }

        return declaration
    }

    fun getNewType(typeModel: TypeModel): TypeModel {
        if (typeModel is TypeValueModel) {
            return lambdas[typeModel.value]?.copy(nullable = typeModel.nullable) ?: typeModel
        }
        return typeModel
    }
}

private class GeneratedInterfaceReplacementLowering(private val context: GeneratedInterfaceCollector) : ModelWithOwnerTypeLowering {
    override fun lowerTypeModel(ownerContext: NodeOwner<TypeModel>): TypeModel {
        val type = super.lowerTypeModel(ownerContext)
        return context.getNewType(type)
    }
}

class ReplaceSimpleGeneratedInterfacesWithLambdas : ModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        val collector = GeneratedInterfaceCollector()
        collector.lower(source)
        return GeneratedInterfaceReplacementLowering(collector).lower(source)
    }
}