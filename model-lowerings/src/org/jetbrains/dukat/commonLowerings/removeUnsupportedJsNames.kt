package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

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


private val STARTS_WITH_NUMBER = "^`\\d+".toRegex()

private fun NameEntity.isInvalidJsName(): Boolean {
    return contains(STARTS_WITH_NUMBER) || contains("-") || startsWith("`:") || startsWith("`.")
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
    } ?: TypeValueModel(IdentifierEntity("Any"), emptyList(), null, null, true)
}

private class UnsupportedJsNamesLowering : ModelWithOwnerTypeLowering {

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>): InterfaceModel {
        val declaration = ownerContext.node
        val (unsupportedMembers, supportedMembers) = declaration.members.partition { member ->
            val name = when (member) {
                is MethodModel -> member.name
                is PropertyModel -> member.name
                else -> null
            }

            name?.startsWith("`") == true && name.isInvalidJsName()
        }

        val commonType = resolveCommonType(unsupportedMembers)

        val resolvedMembers = if (unsupportedMembers.isNotEmpty()) {

            val unsupportedGetter = MethodModel(
                    name = IdentifierEntity("get"),
                    parameters = listOf(ParameterModel(
                            name = "key",
                            type = TypeValueModel(IdentifierEntity("String"), emptyList(), null, null),
                            initializer = null,
                            vararg = false
                    )),
                    type = commonType,
                    typeParameters = emptyList(),
                    static = false,
                    override = null,
                    operator = true,
                    annotations = emptyList(),
                    open = false
            )

            val unsupportedSetter = MethodModel(
                    name = IdentifierEntity("set"),
                    parameters = listOf(ParameterModel(
                            name = "key",
                            type = TypeValueModel(IdentifierEntity("String"), emptyList(), null, null),
                            initializer = null,
                            vararg = false
                    ), ParameterModel(
                            name = "value",
                            type = commonType,
                            initializer = null,
                            vararg = false
                    )),
                    type = TypeValueModel(IdentifierEntity("Unit"), emptyList(), null, null),
                    typeParameters = emptyList(),
                    static = false,
                    override = null,
                    operator = true,
                    annotations = emptyList(),
                    open = false
            )

            listOf(unsupportedGetter, unsupportedSetter) + supportedMembers
        } else {
            supportedMembers
        }

        val declarationLowered = declaration.copy(
                members = resolvedMembers
        )

        return super.lowerInterfaceModel(ownerContext.copy(node = declarationLowered))
    }

}

fun ModuleModel.removeUnsupportedJsNames(): ModuleModel {
    return UnsupportedJsNamesLowering().lowerRoot(this, NodeOwner(this, null))
}

fun SourceSetModel.removeUnsupportedJsNames(): SourceSetModel = transform { it.removeUnsupportedJsNames() }