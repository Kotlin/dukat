package org.jetbrains.dukat.descriptors

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.AbbreviatedType
import org.jetbrains.kotlin.types.ClassTypeConstructorImpl
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.getAbbreviation
import org.jetbrains.kotlin.types.isError

private fun KotlinType.containsError(): Boolean {
    val unwrapped = unwrap()
    return isError || unwrapped.arguments.any { it.type.containsError() } ||
            (unwrapped is AbbreviatedType && unwrapped.expandedType.containsError())
}

private fun TypeParameterDescriptor.containsError(): Boolean {
    return upperBounds.any { it.containsError() }
}

private fun CallableMemberDescriptor.containsError(): Boolean {
    return (valueParameters.map { it.type } + returnType + returnType?.getAbbreviation()).filterNotNull().any { it.containsError() } ||
            typeParameters.any { it.containsError() }
}

private fun TypeAliasDescriptor.containsError(): Boolean {
    return expandedType.containsError()
}

internal fun MemberScope.removeErrorDescriptors() {
    val allMembers = DescriptorUtils.getAllDescriptors(this)
    val callableMembers = allMembers.filterIsInstance<CallableMemberDescriptor>()
    val typeAliases = allMembers.filterIsInstance<TypeAliasDescriptor>()

    val classes = allMembers.filterIsInstance<CustomClassDescriptor>()
    classes.forEach { clazz ->
        clazz.unsubstitutedMemberScope.removeErrorDescriptors()
        clazz.removeConstructors(clazz.constructors.filter { it.containsError() })
        val typeConstructor = clazz.typeConstructor
        if (typeConstructor.supertypes.any { it.containsError() }) {
            clazz.typeConstructor = ClassTypeConstructorImpl(
                clazz,
                typeConstructor.parameters,
                typeConstructor.supertypes.filter { !it.containsError() },
                LockBasedStorageManager.NO_LOCKS
            )
        }
    }

    val membersToRemove = callableMembers.filter { it.containsError() } +
            typeAliases.filter { it.containsError() }
    (this as MutableMemberScope).removeMembers(membersToRemove)
}
