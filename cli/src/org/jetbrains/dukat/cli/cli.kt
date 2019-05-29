package org.jetbrains.dukat.cli

import org.jetbrains.dukat.ast.model.nodes.processing.process
import org.jetbrains.dukat.ast.model.nodes.processing.toNameEntity
import org.jetbrains.dukat.ast.model.nodes.processing.translate
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.compiler.createGraalTranslator
import org.jetbrains.dukat.compiler.translator.TypescriptInputTranslator
import org.jetbrains.dukat.panic.PanicMode
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.panic.setPanicMode
import org.jetbrains.dukat.translatorString.LINE_SEPARATOR
import translateModule
import java.io.File


private fun unescape(name: String): String {
    return name.replace("(?:^`)|(?:`$)".toRegex(), "")
}


private fun NameEntity.updatePackageNameWithPrefix(basePackage: NameEntity): NameEntity {
    return when (this) {
        is IdentifierEntity -> basePackage
        is QualifierEntity -> copy(left = left.updatePackageNameWithPrefix(basePackage))
        else -> raiseConcern("unsupported org.jetbrains.dukat.astCommon.NameEntity ${this}") { this }
    }
}

private fun compile(filename: String, outDir: String?, basePackageName: NameEntity?, translator: TypescriptInputTranslator) {
    println("Converting $filename")

    val sourceSetModel = translator.translate(filename)

    println("Save declarations:")

    sourceSetModel.sources.forEach { source ->
        println("--- ${source.fileName} -----------")

        val modules = source.root.flattenDeclarations()

        modules.forEach { originalModule ->

            val module = if (basePackageName == null) originalModule else originalModule.copy(packageName = originalModule.packageName.updatePackageNameWithPrefix(basePackageName))

            val targetName = "${module.packageName.process(::unescape).translate()}.kt"

            val dir = outDir ?: "./"

            val dirFile = File(dir)
            dirFile.mkdirs()

            val resolvedTarget = dirFile.resolve(targetName)
            println("${resolvedTarget}")

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

private fun printVersion() {
    println("""
dukat version ${System.getProperty("dukat.cli.internal.version")}
""".trimIndent())
}

private fun printUsage(program: String) {
    println("""
Usage: $program [<options>] <d.ts files>

where possible options include:
    -p  <qualifiedPackageName>      package name for the generated file (by default filename.d.ts renamed to filename.d.kt)
    -d  <path>                      destination directory for files with converted declarations (by default declarations are generated in current directory)
    -v, -version                    print version
""".trimIndent())
}


private enum class Engine {
    GRAAL
}

private data class CliOptions(
        val sources: List<String>,
        val outDir: String?,
        val engine: Engine,
        val basePackageName: NameEntity?
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
    var engine = Engine.GRAAL
    var basePackageName: NameEntity? = null
    while (argsIterator.hasNext()) {
        val arg = argsIterator.next()

        when (arg) {
            "-v", "-version" -> printVersion()
            "--always-fail" -> {
                setPanicMode(PanicMode.ALWAYS_FAIL)
            }
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
                printWarning("'-js' is an obsolete flag and planned to be removed")
                engine = when (engineId) {
                    "graal" -> Engine.GRAAL
                    "j2v8" -> {
                        printWarning("j2v8 backend is not supported any more, '-js' option is ignored")
                        Engine.GRAAL
                    }
                    else -> {
                        Engine.GRAAL
                    }
                }
            }

            "-p" -> {
                val packageNameString = argsIterator.readArg()

                if (packageNameString == null) {
                    printError("'-p' should be followed with the qualified package name")
                    return null
                }

                basePackageName = packageNameString.toNameEntity()
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
        options.engine == Engine.GRAAL

        options.sources.forEach { sourceName ->
            compile(sourceName, options.outDir, options.basePackageName, when (options.engine) {
                Engine.GRAAL -> createGraalTranslator()
            })
        }
    }


}