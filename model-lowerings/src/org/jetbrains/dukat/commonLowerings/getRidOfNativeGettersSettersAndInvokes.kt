package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassLikeReferenceModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.ParameterModifierModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.expressions.CallExpressionModel
import org.jetbrains.dukat.astModel.expressions.IdentifierExpressionModel
import org.jetbrains.dukat.astModel.expressions.IndexExpressionModel
import org.jetbrains.dukat.astModel.expressions.PropertyAccessExpressionModel
import org.jetbrains.dukat.astModel.expressions.ThisExpressionModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.AssignmentStatementModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.model.commonLowerings.TopLevelModelLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.ownerContext.wrap

private fun MethodModel.isNativeGetter(): Boolean {
    return operator && (name == IdentifierEntity("get")) && (annotations.contains(AnnotationModel("nativeGetter", emptyList())))
}

private fun MethodModel.isNativeSetter(): Boolean {
    return operator && (name == IdentifierEntity("set")) && (annotations.contains(AnnotationModel("nativeSetter", emptyList())))
}


private fun MethodModel.inlineParams(): List<ParameterModel> {
    return parameters.map { param ->
        when (param.type) {
            is FunctionTypeModel -> param.copy(modifier = ParameterModifierModel.NOINLINE)
            else -> param
        }
    }
}

private fun MethodModel.convertFromNativeGetter(classLikeOwner: ClassLikeModel, moduleOwner: ModuleModel): FunctionModel {
    val classLikeReference = ClassLikeReferenceModel(
            moduleOwner.name.appendLeft(classLikeOwner.name),
            classLikeOwner.typeParameters.mapNotNull {
                val type = it.type
                when (type) {
                    is TypeValueModel -> type.value
                    is TypeParameterReferenceModel -> type.name
                    else -> null
                }
            }
    )

    return FunctionModel(
            name = name,
            parameters = inlineParams(),
            typeParameters = classLikeOwner.typeParameters,
            type = type,
            annotations = mutableListOf(),
            export = false,
            inline = true,
            operator = true,
            visibilityModifier = VisibilityModifierModel.PUBLIC,
            comment = null,
            extend = classLikeReference,
            external = false,
            body = BlockStatementModel(listOf(ReturnStatementModel(IndexExpressionModel(
                    CallExpressionModel(
                            PropertyAccessExpressionModel(
                                    left = ThisExpressionModel(),
                                    right = IdentifierExpressionModel(IdentifierEntity("asDynamic"))
                            ),
                            listOf()
                    ),
                    IdentifierExpressionModel(
                            IdentifierEntity(parameters[0].name)
                    )
            ))))
    )
}

private fun MethodModel.convertFromNativeSetter(classLikeOwner: ClassLikeModel, moduleOwner: ModuleModel): FunctionModel {
    val classLikeReference = ClassLikeReferenceModel(
            moduleOwner.name.appendLeft(classLikeOwner.name),
            classLikeOwner.typeParameters.mapNotNull {
                val type = it.type
                when (type) {
                    is TypeValueModel -> type.value
                    is TypeParameterReferenceModel -> type.name
                    else -> null
                }
            }
    )

    return FunctionModel(
            name = name,
            parameters = inlineParams(),
            typeParameters = classLikeOwner.typeParameters,
            type = type,
            annotations = mutableListOf(),
            export = false,
            inline = true,
            operator = true,
            visibilityModifier = VisibilityModifierModel.PUBLIC,
            comment = null,
            extend = classLikeReference,
            external = false,
            body = BlockStatementModel(listOf(AssignmentStatementModel(IndexExpressionModel(
                    CallExpressionModel(
                            PropertyAccessExpressionModel(
                                    left = ThisExpressionModel(),
                                    right = IdentifierExpressionModel(IdentifierEntity("asDynamic"))
                            ),
                            listOf()
                    ),
                    IdentifierExpressionModel(
                            IdentifierEntity(parameters[0].name)
                    )
            ), IdentifierExpressionModel(IdentifierEntity(parameters[1].name)))))
    )
}


class RemoveNativeGettersSettersAndInvokes : TopLevelModelLowering {

    private fun ClassLikeModel.resolveMembers(parentModule: ModuleModel): Pair<List<MemberModel>, List<FunctionModel>> {

        val generatedFunctions = mutableListOf<FunctionModel>()

        val membersResolved = members.mapNotNull {
            when (it) {
                is MethodModel -> {
                    when {
                        it.isNativeGetter() -> {
                            generatedFunctions.add(it.convertFromNativeGetter(this, parentModule))
                            null
                        }
                        it.isNativeSetter() -> {
                            generatedFunctions.add(it.convertFromNativeSetter(this, parentModule))
                            null
                        }
                        else -> {
                            it
                        }
                    }
                }
                else -> it
            }
        }

        return Pair(membersResolved, generatedFunctions)
    }

    fun processInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): List<TopLevelModel> {
        val node = ownerContext.node
        val (members, generatedFunctions) = node.resolveMembers(parentModule)
        return listOf(node.copy(members = members)) + generatedFunctions
    }

    fun processClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): List<TopLevelModel> {
        val node = ownerContext.node
        val (members, generatedFunctions) = node.resolveMembers(parentModule)
        return listOf(node.copy(members = members)) + generatedFunctions
    }

    override fun lowerTopLevelDeclarations(declarations: List<TopLevelModel>, ownerContext: NodeOwner<ModuleModel>, parentModule: ModuleModel): List<TopLevelModel> {
        return declarations.flatMap {
            when (it) {
                is InterfaceModel -> processInterfaceModel(ownerContext.wrap(it), parentModule)
                is ClassModel -> processClassModel(ownerContext.wrap(it), parentModule)
                else -> listOf(it)
            }
        }
    }
}

class GetRidOfNativeGettersSettersAndInvokes : TopLevelModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        val sourceSet = RemoveNativeGettersSettersAndInvokes().lower(source)
        return sourceSet
    }
}