package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.ownerContext.NodeOwner


private val logger = Logging.logger("introduceModels")

interface ModelWithOwnerTypeLowering : ModelWithOwnerLowering {

    override fun lowerEnumModel(ownerContext: NodeOwner<EnumModel>): EnumModel {
        return ownerContext.node
    }

    fun lowerMethodModel(ownerContext: NodeOwner<MethodModel>): MethodModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterModel(NodeOwner(parameter, ownerContext)) },
                type = lowerTypeModel(NodeOwner(declaration.type, ownerContext)),
                typeParameters = declaration.typeParameters.map { lowerTypeParameterModel(ownerContext.wrap(it)) }
        )
    }

    fun lowerPropertyModel(ownerContext: NodeOwner<PropertyModel>): PropertyModel {
        val declaration = ownerContext.node
        return declaration.copy(
            type = lowerTypeModel(NodeOwner(declaration.type, ownerContext)),
            typeParameters = declaration.typeParameters.map { lowerTypeParameterModel(ownerContext.wrap(it)) }
        )
    }

    override fun lowerMemberModel(ownerContext: NodeOwner<MemberModel>): MemberModel {
        return when (val declaration = ownerContext.node) {
            is MethodModel -> lowerMethodModel(NodeOwner(declaration, ownerContext))
            is PropertyModel -> lowerPropertyModel(NodeOwner(declaration, ownerContext))
            is ConstructorModel -> lowerConstructorModel(NodeOwner(declaration, ownerContext))
            is ClassLikeModel -> lowerClassLikeModel(NodeOwner(declaration, ownerContext))
            else -> {
                logger.trace("skipping $declaration")
                declaration
            }
        }
    }

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>): FunctionModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterModel(NodeOwner(parameter, ownerContext)) },
                type = lowerTypeModel(NodeOwner(declaration.type, ownerContext)),
                typeParameters = declaration.typeParameters.map { lowerTypeParameterModel(ownerContext.wrap(it)) }
        )
    }

    override fun lowerFunctionTypeModel(ownerContext: NodeOwner<FunctionTypeModel>): FunctionTypeModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterModel(NodeOwner(parameter, ownerContext)) },
                type = lowerTypeModel(NodeOwner(declaration.type, ownerContext))
        )
    }

    override fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel {
        val declaration = ownerContext.node
        return declaration.copy(type = lowerTypeModel(NodeOwner(declaration.type, ownerContext)))
    }

    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>): VariableModel {
        val declaration = ownerContext.node
        return declaration.copy(
            type = lowerTypeModel(NodeOwner(declaration.type, ownerContext)),
            typeParameters = declaration.typeParameters.map { lowerTypeParameterModel(ownerContext.wrap(it)) }
        )
    }

    fun lowerHeritageModel(ownerContext: NodeOwner<HeritageModel>): HeritageModel {
        val heritageClause = ownerContext.node
        val typeParams = heritageClause.typeParams.map {
            lowerTypeModel(ownerContext.wrap(it))
        }
        return heritageClause.copy(
            value = lowerTypeValueModel(ownerContext.wrap(heritageClause.value)),
            typeParams = typeParams
        )
    }


    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>): InterfaceModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberModel(NodeOwner(member, ownerContext)) },
                typeParameters = declaration.typeParameters.map { typeParameterModel ->  lowerTypeParameterModel(ownerContext.wrap(typeParameterModel)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageModel(NodeOwner(heritageClause, ownerContext))
                }
        )
    }

    override fun lowerTypeAliasModel(ownerContext: NodeOwner<TypeAliasModel>, moduleOwner: ModuleModel): TypeAliasModel {
        val declaration = ownerContext.node
        return declaration.copy(
            typeReference = lowerTypeModel(ownerContext.wrap(declaration.typeReference)),
            typeParameters = declaration.typeParameters.map { typeParameterModel ->  lowerTypeParameterModel(ownerContext.wrap(typeParameterModel)) }
        )
    }

    fun lowerConstructorModel(ownerContext: NodeOwner<ConstructorModel>): ConstructorModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterModel(NodeOwner(parameter, ownerContext)) }
        )
    }

    override fun lowerObjectModel(ownerContext: NodeOwner<ObjectModel>): ObjectModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberModel(NodeOwner(member, ownerContext)) }
        )

    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.map { member -> lowerMemberModel(NodeOwner(member, ownerContext)) },
                typeParameters = declaration.typeParameters.map { typeParameterModel ->  lowerTypeParameterModel(ownerContext.wrap(typeParameterModel)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageModel(NodeOwner(heritageClause, ownerContext))
                },
                companionObject = declaration.companionObject?.let { lowerObjectModel(ownerContext.wrap(it)) }
        )
    }
}
