package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class AnnotationModel(
        val name: String,
        val params: List<NameEntity>
) : Entity {
    companion object {
        val NATIVE_GETTER  = AnnotationModel("nativeGetter", emptyList())
        val NATIVE_SETTER  = AnnotationModel("nativeSetter", emptyList())
        val NATIVE_INVOKE  = AnnotationModel("nativeInvoke", emptyList())
    }
}

