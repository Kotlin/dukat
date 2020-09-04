package org.jetbrains.dukat.descriptors

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isError

private fun KotlinType.containsError(): Boolean {
    return isError || arguments.any { it.type.containsError() }
}

private fun CallableMemberDescriptor.containsError(): Boolean {
    return (valueParameters.map { it.type } + returnType).filterNotNull().any { it.containsError() }
}

internal fun MemberScope.removeErrorDescriptors() {
    val allMembers = DescriptorUtils.getAllDescriptors(this)
    val callableMembers = allMembers.filterIsInstance<CallableMemberDescriptor>()
    val classes = allMembers.filterIsInstance<CustomClassDescriptor>()
    classes.forEach { clazz ->
        clazz.unsubstitutedMemberScope.removeErrorDescriptors()
        clazz.removeConstructors(clazz.constructors.filter { it.containsError() })
    }
    val membersToRemove = callableMembers.filter { it.containsError() }
    (this as MutableMemberScope).removeMembers(membersToRemove)
}