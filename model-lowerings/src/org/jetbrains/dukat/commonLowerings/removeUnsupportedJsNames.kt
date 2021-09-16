package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.stdlib.asKotlinType

private fun NameEntity.startsWith(prefix: String): Boolean {
    return when (this) {
        is IdentifierEntity -> value.startsWith(prefix)
        is QualifierEntity -> right.startsWith(prefix)
    }
}

private fun NameEntity.contains(pattern: Regex): Boolean {
    return when (this) {
        is IdentifierEntity -> value.contains(pattern)
        is QualifierEntity -> left.contains(pattern)
    }
}

private fun NameEntity.contains(str: String): Boolean {
    return when (this) {
        is IdentifierEntity -> value.contains(str)
        is QualifierEntity -> left.contains(str)
    }
}

private fun Char.isLetterOrDigitOrUnderscore(): Boolean {
   return isLetterOrDigit() || equals('_')
}

private fun String.isEscaped(): Boolean {
    return startsWith('`') && endsWith('`')
}

private fun String.unescaped(): String {
    return when {
        isEscaped() -> substring(1 until lastIndex)
        else -> this
    }
}

private fun IdentifierEntity.isValidForJs(): Boolean {
    return with(value.unescaped()) {
        isNotEmpty() && all(Char::isLetterOrDigitOrUnderscore) && !first().isDigit()
    }
}

private val STARTS_WITH_NUMBER = "^`\\d+".toRegex()

private fun NameEntity.isInvalidJsName(): Boolean {
    return when (this) {
        is IdentifierEntity -> !isValidForJs()
        is QualifierEntity -> contains(STARTS_WITH_NUMBER) ||
                contains("-") ||
                startsWith("`:") ||
                startsWith("`.")
    }
}

private fun MemberModel.getType(): TypeModel? {
    return when (this) {
        is MethodModel -> type
        is PropertyModel -> type
        else -> null
    }
}

private fun resolveCommonType(members: List<MemberModel>): TypeModel {
    val firstMember = members.firstOrNull()
    val firstMemberType = firstMember?.getType()

    return firstMemberType?.let { firstType ->
        if (members.all { member -> member.getType() == firstMemberType }) {
            firstType
        } else null
    } ?: TypeValueModel("Any".asKotlinType(), emptyList(), null, null, true)
}

private fun TypeModel.makeNullable(): TypeModel {
    return when(this) {
        is TypeValueModel -> copy(nullable = true)
        is FunctionTypeModel -> copy(nullable = true)
        is TypeParameterReferenceModel -> copy(nullable = true)
        is TypeParameterModel -> copy(nullable = true)
        else -> this
    }
}

private class UnsupportedJsNamesLowering : ModelWithOwnerTypeLowering {

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
        val declaration = ownerContext.node
        val (unsupportedMembers, supportedMembers) = declaration.members.partition { member ->
            val name = when (member) {
                is MethodModel -> member.name
                is PropertyModel -> member.name
                else -> null
            }

            name?.isInvalidJsName() == true
        }

        val commonType = resolveCommonType(unsupportedMembers)

        val resolvedMembers = if (unsupportedMembers.isNotEmpty()) {

            val unsupportedGetter = MethodModel(
                    name = IdentifierEntity("get"),
                    parameters = listOf(ParameterModel(
                            name = "key",
                            type = TypeValueModel(IdentifierEntity("String"), emptyList(), null, null),
                            initializer = null,
                            vararg = false,
                            modifier = null
                    )),
                    type = commonType.makeNullable(),
                    typeParameters = emptyList(),
                    static = false,
                    override = null,
                    operator = true,
                    annotations = listOf(AnnotationModel.NATIVE_GETTER),
                    open = false,
                    body = null
            )

            val unrolledTypes = unsupportedMembers.mapNotNull { it.getType() }

            val unsupportedSetters = unrolledTypes.map {setterType ->
                MethodModel(
                        name = IdentifierEntity("set"),
                        parameters = listOf(ParameterModel(
                                name = "key",
                                type = TypeValueModel(IdentifierEntity("String"), emptyList(), null, null),
                                initializer = null,
                                vararg = false,
                                modifier = null
                        ), ParameterModel(
                                name = "value",
                                type = setterType,
                                initializer = null,
                                vararg = false,
                                modifier = null
                        )),
                        type = TypeValueModel(IdentifierEntity("Unit"), emptyList(), null, null),
                        typeParameters = emptyList(),
                        static = false,
                        override = null,
                        operator = true,
                        annotations = listOf(AnnotationModel.NATIVE_SETTER),
                        open = false,
                        body = null
                )
            }

            listOf(unsupportedGetter) + unsupportedSetters + supportedMembers
        } else {
            supportedMembers
        }

        val declarationLowered = declaration.copy(
                members = resolvedMembers
        )

        return super.lowerInterfaceModel(ownerContext.copy(node = declarationLowered), parentModule)
    }

}

class RemoveUnsupportedJsNames : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return UnsupportedJsNamesLowering().lowerRoot(module, NodeOwner(module, null))
    }
}