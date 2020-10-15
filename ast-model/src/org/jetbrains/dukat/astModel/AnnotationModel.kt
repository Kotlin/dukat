package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.toNameEntity

data class AnnotationModel(
        val name: NameEntity,
        val params: List<NameEntity>,
        val target: AnnotationTarget? = null
        ) : Entity {
    companion object {
        val NATIVE_GETTER  = AnnotationModel("nativeGetter".toNameEntity(), emptyList())
        val NATIVE_SETTER  = AnnotationModel("nativeSetter".toNameEntity(), emptyList())
        val NATIVE_INVOKE  = AnnotationModel("nativeInvoke".toNameEntity(), emptyList())
    }
}

