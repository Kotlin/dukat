package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ImportModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.visitors.visitTopLevelModel

private fun InterfaceModel.hasNestedEntity(): Boolean {
    if ((companionObject?.members?.isNotEmpty() == true) || (companionObject?.parentEntities?.isNotEmpty() == true)) {
        return true
    }

    return members.any { (it is ClassLikeModel) }
}

private fun ClassLikeModel.hasDelegation(): Boolean {
    return companionObject?.parentEntities?.any { it.delegateTo != null } == true
}

class AddStandardImportsAndAnnotations(private val addSuppressAnnotations: Boolean) : ModelLowering {
    private fun ModuleModel.addStandardImportsAndAnnotations() {

        if (addSuppressAnnotations) {
            annotations.add(AnnotationModel("file:Suppress", listOf(
                    "INTERFACE_WITH_SUPERCLASS",
                    "OVERRIDING_FINAL_MEMBER",
                    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
                    "CONFLICTING_OVERLOADS"
            ).map { it.toNameEntity() }))
        }

        imports.addAll(0,
                listOf(
                        "kotlin.js.*",
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

    override fun lower(module: ModuleModel): ModuleModel {
        module.visitTopLevelModel { topLevelModel ->
            when (topLevelModel) {
                is InterfaceModel -> {
                    val supressionAnnotations = mutableListOf<IdentifierEntity>()

                    if (topLevelModel.hasDelegation()) {
                        supressionAnnotations.add(IdentifierEntity("EXTERNAL_DELEGATION"))
                    }

                    if (topLevelModel.hasNestedEntity()) {
                        supressionAnnotations.add(IdentifierEntity("NESTED_CLASS_IN_EXTERNAL_INTERFACE"))
                    }

                    if (supressionAnnotations.isNotEmpty()) {
                        topLevelModel.annotations.add(AnnotationModel("Suppress", supressionAnnotations))
                    }
                }
                is ModuleModel -> {
                    topLevelModel.addStandardImportsAndAnnotations()
                }
            }
        }

        return module
    }
}
