package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.KotlinType

class CustomClassDescriptor(
    parent: PackageFragmentDescriptor,
    name: NameEntity,
    modality: Modality,
    classKind: ClassKind,
    parentTypes: List<KotlinType>,
    private val isCompanion: Boolean,
    private val companionObject: ClassDescriptor?,
    private val typeParameters: List<TypeParameterDescriptor>,
    override val annotations: Annotations
) :
    ClassDescriptorImpl(
        parent,
        if (isCompanion) {
            Name.identifier("Companion")
        } else {
            Name.identifier(translateName(name))
        },
        modality,
        classKind,
        parentTypes,
        SourceElement.NO_SOURCE,
        !isCompanion,
        LockBasedStorageManager.NO_LOCKS
    ) {
    override fun isCompanionObject(): Boolean {
        return isCompanion
    }

    override fun getCompanionObjectDescriptor(): ClassDescriptor? {
        return companionObject
    }

    override fun getDeclaredTypeParameters(): List<TypeParameterDescriptor> {
        return typeParameters
    }
}