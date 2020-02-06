package org.jetbrains.dukat.descriptors.versionSpecific

import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.load.kotlin.toSourceElement
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance

object VersionSpecificDescriptorAPI {
    fun createForFurtherModification(
        parent: DeclarationDescriptor,
        variance: Variance,
        name: String,
        index: Int
    ): TypeParameterDescriptorImpl {
        return TypeParameterDescriptorImpl.createForFurtherModification(
            parent,
            Annotations.EMPTY,
            false,
            variance,
            Name.identifier(name),
            index,
            parent.toSourceElement,
            LockBasedStorageManager.NO_LOCKS
        )
    }
}