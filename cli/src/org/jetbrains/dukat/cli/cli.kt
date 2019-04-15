package org.jetbrains.dukat.cli

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.process
import org.jetbrains.dukat.ast.model.nodes.toNameNode
import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.compiler.createNashornTranslator
import org.jetbrains.dukat.compiler.createV8Translator
import org.jetbrains.dukat.compiler.translator.TypescriptInputTranslator
import org.jetbrains.dukat.translatorString.LINE_SEPARATOR
import translateModule
import java.io.File


private fun unescape(name: String): String {
    return name.replace("(?:^`)|(?:`$)".toRegex(), "")
}


private fun NameNode.updatePackageNameWithPrefix(basePackage: NameNode): NameNode {
    return when (this) {
        is IdentifierNode -> basePackage
        is QualifiedNode -> copy(left = left.updatePackageNameWithPrefix(basePackage))
        else -> throw Exception("unsupported NameNode ${this}")
    }
}

private fun compile(filename: String, outDir: String?, basePackageName: NameNode?, translator: TypescriptInputTranslator) {
    println("Converting $filename")

    val sourceSetModel = translator.translate(filename)

    println("Save declarations:")

    sourceSetModel.sources.forEach { source ->
        println("--- ${source.fileName} -----------")

        val modules = source.root.flattenDeclarations()

        modules.forEach { originalModule ->

            val module = if (basePackageName == null) originalModule else originalModule.copy(packageName = originalModule.packageName.updatePackageNameWithPrefix(basePackageName))

            println("MODULE MODULE ${module.packageName.updatePackageNameWithPrefix(QualifiedNode(IdentifierNode("ping"), IdentifierNode("pong"))).translate()}")

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

            resolvedTarget.writeText(translateModule(module).joinToString(LINE_SEPARATOR))
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
    -b  <qualifiedPackageName>   package name for the generated file (by default filename.d.ts renamed to filename.d.kt)
    -d  <path>                   destination directory for files with converted declarations (by default declarations are generated in current directory)
    -js nashorn | j2v8           js-interop JVM engine (nashorn by default)
""".trimIndent())
}


private enum class Engine {
    NASHORN, J2V8
}

private data class CliOptions(
        val sources: List<String>,
        val outDir: String?,
        val engine: Engine,
        val basePackageName: NameNode?
)


private fun printError(message: String) {
    System.err.println(message)
}

private fun printWarning(message: String) {
    System.out.println("Warning: ${message}")
}

private fun process(args: List<String>): CliOptions? {
    val argsIterator = args.iterator()

    val sources = mutableListOf<String>()
    var outDir: String? = null
    var engine = Engine.NASHORN
    var basePackageName: NameNode? = null
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

            "-js" -> {
                val engineId = argsIterator.readArg()
                engine = when (engineId) {
                    "nashorn" -> Engine.NASHORN
                    "j2v8" -> {
                        printWarning("currently j2v8 backend is not supported on windows-based platforms")
                        Engine.J2V8
                    }
                    else -> {
                        printError("'-js' can be set to either 'nashorn' or 'j2v8'")
                        return null
                    }
                }
            }

            "-p" -> {
                val packageNameString = argsIterator.readArg()

                if (packageNameString == null) {
                    printError("'-p' should be followed with the qualified package name")
                    return null
                }

                basePackageName = packageNameString.toNameNode()
            }

            else -> sources.add(arg)
        }
    }

    return CliOptions(sources, outDir, engine, basePackageName)
}

fun main(vararg args: String) {
    if (args.isEmpty()) {
        printUsage("dukat")
        return
    }


    process(args.toList())?.let { options ->
        options.engine == Engine.J2V8

        options.sources.forEach { sourceName ->
            compile(sourceName, options.outDir, options.basePackageName, when (options.engine) {
                Engine.NASHORN -> createNashornTranslator()
                Engine.J2V8 -> createV8Translator()
            })
        }
    }


}