package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.js.config.JsConfig
import org.jetbrains.kotlin.types.TypeConstructor

class DescriptorContext(val config: JsConfig) {

    private val registeredDescriptors: MutableMap<NameEntity, ClassDescriptor> = mutableMapOf()
    private val registeredTypeAliases: MutableMap<NameEntity, TypeAliasDescriptor> = mutableMapOf()
    private val typeParameters: MutableMap<NameEntity, TypeParameterDescriptor> = mutableMapOf()
    val registeredImports: MutableList<String> = mutableListOf()

    fun registerDescriptor(name: NameEntity, descriptor: ClassDescriptor) {
        registeredDescriptors[name] = descriptor
    }

    fun registerTypeAlias(name: NameEntity, descriptor: TypeAliasDescriptor) {
        registeredTypeAliases[name] = descriptor
    }

    fun getDescriptor(name: NameEntity): ClassDescriptor? {
        return registeredDescriptors[name]
    }

    fun getTypeAlias(name: NameEntity): TypeAliasDescriptor? {
        return registeredTypeAliases[name]
    }

    fun getTypeAliasDescriptorByConstructor(typeConstructor: TypeConstructor): TypeAliasDescriptor? {
        return registeredTypeAliases.filter { (_, descriptor) -> descriptor.defaultType.constructor == typeConstructor }.values.firstOrNull()
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