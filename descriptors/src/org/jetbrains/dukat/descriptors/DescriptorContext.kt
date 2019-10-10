package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.kotlin.descriptors.ClassDescriptor

class DescriptorContext {
    private val registeredDescriptors: MutableMap<NameEntity, ClassDescriptor> = mutableMapOf()

    fun registerDescriptor(name: NameEntity, descriptor: ClassDescriptor) {
        registeredDescriptors[name] = descriptor
    }

    fun getDescriptor(name: NameEntity): ClassDescriptor? {
        return registeredDescriptors[name]
    }

}