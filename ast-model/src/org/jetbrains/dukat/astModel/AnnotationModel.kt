package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.toNameEntity

data class AnnotationModel(
        val name: String,
        val params: List<NameEntity>,
        val target: AnnotationTarget? = null
        ) : Entity {
    companion object {
        val NATIVE_GETTER  = AnnotationModel("nativeGetter", emptyList())
        val NATIVE_SETTER  = AnnotationModel("nativeSetter", emptyList())
        val NATIVE_INVOKE  = AnnotationModel("nativeInvoke", emptyList())
    }
}

