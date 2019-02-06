package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode


//TODO: this should be done somewhere near escapeIdentificators (at least code should be reused)
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
    return name.replace("(?:^\"|\')|(?:\"|\'$)".toRegex(), "")
}

fun DocumentRootNode.introduceModuleMetadata(): DocumentRootNode {

    val parentDocRoots = generateSequence(this) { it.owner }.asIterable().reversed()
    val packageNames = parentDocRoots.map { unquote(it.packageName) }

    val packageNameResolved = packageNames.joinToString(".") { escapePackageName(it) }
    fullPackageName = packageNameResolved

    isQualifier = (packageName == unquote(packageName))

    showQualifierAnnotation = owner != null
    val qualifier = if (isQualifier) "JsQualifier" else "JsModule"
    qualifierName = packageNames.subList(1, packageNames.size).joinToString(".")

    declarations.forEach { declaration ->
        if (declaration is DocumentRootNode) {
            declaration.introduceModuleMetadata()
        }
    }

    return this
}