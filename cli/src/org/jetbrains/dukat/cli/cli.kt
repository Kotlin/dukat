package org.jetbrains.dukat.cli

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.process
import org.jetbrains.dukat.ast.model.nodes.toNameNode
import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.compiler.createV8Translator
import translateModule
import java.io.File


private fun unescape(name: String): String {
    return name.replace("(?:^`)|(?:`$)".toRegex(), "")
}


private fun compile(filename: String, outDir: String?) {
    println("Converting $filename")
    val translator = createV8Translator()

    val sourceSetModel = translator.translate(filename)

    println("Save declarations:")

    sourceSetModel.sources.forEach { source ->
        println("--- ${source.fileName} -----------")
        val modules = source.root.flattenDeclarations()

        modules.forEach { module ->

            val targetName = "${module.packageName.process(::unescape).translate()}.kt"

            module.annotations.add(AnnotationNode("file:Suppress", listOf(
                    "INTERFACE_WITH_SUPERCLASS",
                    "OVERRIDING_FINAL_MEMBER",
                    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
                    "CONFLICTING_OVERLOADS",
                    "EXTERNAL_DELEGATION",
                    "NESTED_CLASS_IN_EXTERNAL_INTERFACE"
            ).map { it.toNameNode() }))

            module.imports.addAll(
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

            val dir = outDir ?: "./"

            val dirFile = File(dir)
            dirFile.mkdirs()

            val resolvedTarget = dirFile.resolve(targetName)
            println("generating ${resolvedTarget}")

            resolvedTarget.writeText(translateModule(module).joinToString("\n"))
        }
    }
}


fun Iterator<String>.readArg(): String? {
    return if (hasNext()) {
        val value = next()
        if (value.startsWith("-")) null else value
    } else null
}

private fun printUsage(program: String) {
    println("""
Usage: $program [<options>] <d.ts files>

where possible options include:
-d <path>                   destination directory for files with converted declarations (current by default)
""".trimIndent())
}


private data class CliOptions(
        val sources: List<String>,
        val outDir: String?
)


private fun printError(message: String) {
    System.err.println(message)
}

private fun process(args: List<String>): CliOptions? {
    val argsIterator = args.iterator()

    val sources = mutableListOf<String>()
    var outDir: String? = null
    while (argsIterator.hasNext()) {
        val arg = argsIterator.next()

        when (arg) {
            "-d" -> {
                val outDirArg = argsIterator.readArg()
                if (outDirArg == null) {
                    printError("'-d' should be followed by path to destination directory")
                    return null
                } else {
                    outDir = outDirArg
                }
            }
            else -> sources.add(arg)
        }
    }

    return CliOptions(sources, outDir)
}

fun main(vararg args: String) {
    if (args.isEmpty()) {
        printUsage("dukat")
        return
    }

    process(args.toList())?.let { options ->
        options.sources.forEach { sourceName ->
            compile(sourceName, options.outDir)
        }
    }


}