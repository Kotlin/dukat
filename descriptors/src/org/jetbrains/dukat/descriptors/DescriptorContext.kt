package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor

class DescriptorContext {
    private val registeredDescriptors: MutableMap<NameEntity, ClassDescriptor> = mutableMapOf()
    private val typeParameters: MutableMap<NameEntity, TypeParameterDescriptor> = mutableMapOf()

    fun registerDescriptor(name: NameEntity, descriptor: ClassDescriptor) {
        registeredDescriptors[name] = descriptor
    }

    fun getDescriptor(name: NameEntity): ClassDescriptor? {
        return registeredDescriptors[name]
    }

    fun registerTypeParameter(name: NameEntity, descriptor: TypeParameterDescriptor) {
        typeParameters[name] = descriptor
    }

    fun getTypeParameter(name: NameEntity): TypeParameterDescriptor? {
        return typeParameters[name]
    }

    fun removeTypeParameter(name: NameEntity) {
        typeParameters.remove(name)
    }

}