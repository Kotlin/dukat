package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ImportModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.visitors.visitTopLevelModel

private fun ModuleModel.addStandardImportsAndAnnotations() {

    annotations.add(AnnotationModel("file:Suppress", listOf(
            "INTERFACE_WITH_SUPERCLASS",
            "OVERRIDING_FINAL_MEMBER",
            "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
            "CONFLICTING_OVERLOADS",
            "EXTERNAL_DELEGATION"
    ).map { it.toNameEntity() }))

    imports.addAll(0,
            listOf(
                    "kotlin.js.*",
                    "kotlin.js.Json",
                    "org.khronos.webgl.*",
                    "org.w3c.dom.*",
                    "org.w3c.dom.events.*",
                    "org.w3c.dom.parsing.*",
                    "org.w3c.dom.svg.*",
                    "org.w3c.dom.url.*",
                    "org.w3c.fetch.*",
                    "org.w3c.files.*",
                    "org.w3c.notifications.*",
                    "org.w3c.performance.*",
                    "org.w3c.workers.*",
                    "org.w3c.xhr.*"
            ).map { ImportModel(it.toNameEntity()) }
    )
}

fun InterfaceModel.hasNestedEntity(): Boolean {
    if ((companionObject?.members?.isNotEmpty() == true) || (companionObject?.parentEntities?.isNotEmpty() == true)) {
        return true
    }

    return members.any { (it is ClassLikeModel) }
}

fun SourceSetModel.addStandardImportsAndAnnotations(): SourceSetModel {

    visitTopLevelModel { topLevelModel ->
        when (topLevelModel) {
            is InterfaceModel -> {
                if (topLevelModel.hasNestedEntity()) {
                    topLevelModel.annotations.add(AnnotationModel("Suppress", listOf(IdentifierEntity("NESTED_CLASS_IN_EXTERNAL_INTERFACE"))))
                }
            }
            is ModuleModel -> {
                topLevelModel.addStandardImportsAndAnnotations()
            }
        }
    }

    return this
}


