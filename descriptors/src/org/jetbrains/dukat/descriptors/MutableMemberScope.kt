package org.jetbrains.dukat.descriptors

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScopeImpl
import org.jetbrains.kotlin.utils.Printer

class MutableMemberScope(private val members: MutableList<DeclarationDescriptor>) : MemberScopeImpl() {

    override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? =
        members.filterIsInstance<ClassifierDescriptor>().singleOrNull { it.name == name }

    override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
        members.filterIsInstance<PropertyDescriptor>()
            .filter { it.name == name }

    override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> =
        members.filterIsInstance<SimpleFunctionDescriptor>()
            .filter { it.name == name }

    override fun getContributedDescriptors(
        kindFilter: DescriptorKindFilter,
        nameFilter: (Name) -> Boolean
    ): Collection<DeclarationDescriptor> =
        members.filter { kindFilter.accepts(it) && nameFilter(it.name) }

    fun addMembers(newMembers: List<DeclarationDescriptor>) {
        members += newMembers
    }

    override fun printScopeStructure(p: Printer) {}

}