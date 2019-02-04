package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode

private fun escapePackageName(name: String): String {
    return name
            .replace("/".toRegex(), ".")
            .replace("-".toRegex(), "_")
            .replace("^_$".toRegex(), "`_`")
            .replace("^class$".toRegex(), "`class`")
            .replace("^var$".toRegex(), "`var`")
            .replace("^val$".toRegex(), "`val`")
            .replace("^interface$".toRegex(), "`interface`")
}

private fun unquote(name: String): String {
    return name.replace("(?:^\")|(?:\"$)".toRegex(), "")
}

fun DocumentRootNode.introduceModuleMetadata(): DocumentRootNode {

    val parentDocRoots = generateSequence(this) { it.owner }.asIterable().reversed()
    val packageNames = parentDocRoots.map { unquote(it.packageName) }

    val packageNameResolved = packageNames.joinToString(".") { escapePackageName(it) }
    fullPackageName = packageNameResolved

    if (owner != null) {
        val needsQualifier = packageName == unquote(packageName)
        val qualifier = if (needsQualifier) "JsQualifier" else "JsModule"
        val qualifierName = packageNames.subList(1, packageNames.size).joinToString(".")

        annotations.add(AnnotationNode("file:${qualifier}", listOf(qualifierName)))
    }

    declarations.forEach { declaration ->
        if (declaration is DocumentRootNode) {
            declaration.introduceModuleMetadata()
        }
    }

    return this
}