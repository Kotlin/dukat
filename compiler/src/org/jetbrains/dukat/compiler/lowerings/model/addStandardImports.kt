package org.jetbrains.dukat.compiler.lowerings.model

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.processing.toNameNode
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel

private fun ModuleModel.addStandardImports() {

    annotations.add(AnnotationNode("file:Suppress", listOf(
            "INTERFACE_WITH_SUPERCLASS",
            "OVERRIDING_FINAL_MEMBER",
            "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
            "CONFLICTING_OVERLOADS",
            "EXTERNAL_DELEGATION",
            "NESTED_CLASS_IN_EXTERNAL_INTERFACE"
    ).map { it.toNameNode() }))

    imports.addAll(
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
            ).map { it.toNameNode() }
    )

    sumbodules.forEach { submodule ->  submodule.addStandardImports() }
}

fun SourceSetModel.addStandardImports(): SourceSetModel {

    copy(sources = sources.map { source ->
        source.root.addStandardImports()
        source
    })

    return this
}


