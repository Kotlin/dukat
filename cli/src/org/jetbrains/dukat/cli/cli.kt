package org.jetbrains.dukat.cli

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.compiler.createGraalTranslator
import org.jetbrains.dukat.compiler.translator.IdlInputTranslator
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.panic.PanicMode
import org.jetbrains.dukat.panic.setPanicMode
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import org.jetbrains.dukat.translator.TranslationErrorFileNotFound
import org.jetbrains.dukat.translator.TranslationErrorInvalidFile
import org.jetbrains.dukat.translator.TranslationUnitResult
import org.jetbrains.dukat.translatorString.IDL_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.TS_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.WEBIDL_DECLARATION_EXTENSION
import translateModule
import java.io.File
import kotlin.system.exitProcess


@Serializable
private data class Report(val outputs: List<String>)

private fun TranslationUnitResult.resolveAsError(source: String): String {
    return when (this) {
        is TranslationErrorInvalidFile -> "invalid file name: ${fileName} - only typescript declarations, that is, files with *.d.ts extension can be processed"
        is TranslationErrorFileNotFound -> "file not found: ${fileName}"
        else -> "failed to translate ${source} for unknown reason"
    }
}

private fun compile(filename: String, outDir: String?, translator: InputTranslator, pathToReport: String?) {
    val sourceFile = File(filename)

    val translatedUnits = translateModule(sourceFile.absolutePath, translator)

    val dirFile = File(outDir ?: "./")
    if (translatedUnits.isNotEmpty()) {
        dirFile.mkdirs()
    }

    val buildReport = pathToReport !== null

    val output = mutableListOf<String>()

    translatedUnits.forEach { translationUnitResult ->

        if (translationUnitResult is ModuleTranslationUnit) {
            val targetName = "${translationUnitResult.name}.kt"

            if (targetName != null) {
                val resolvedTarget = dirFile.resolve(targetName)

                println(resolvedTarget.name)

                if (buildReport) {
                    output.add(resolvedTarget.name)
                }

                resolvedTarget.writeText(translationUnitResult.content)
            }
        } else {
            print("ERROR: ${translationUnitResult.resolveAsError(filename)}")
        }
    }

    if (buildReport) {
        saveReport(pathToReport!!, Report(output))
    }
}

private fun saveReport(reportPath: String, report: Report): Boolean {
    val reportFile = File(reportPath)

    val reportBody = Json.indented.stringify(Report.serializer(), report)
    try {
        println("saving report to ${reportFile.absolutePath}")
        reportFile.absoluteFile.parentFile.mkdirs()
        reportFile.writeText(reportBody)
    } catch (e: Exception) {
        printError("Failed to save report with following exception:")
        println(e)
        return false
    }

    return true
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
    -m  String                      use this value as @file:JsModule annotation value whenever such annotation occurs
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
        val basePackageName: NameEntity,
        val jsModuleName: String?,
        val reportPath: String?
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
    var basePackageName: NameEntity = ROOT_PACKAGENAME
    var jsModuleName: String? = null
    var reportPath: String? = null
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
            "-m" -> {
                val packageNameString = argsIterator.readArg()

                if (packageNameString == null) {
                    printError("'-m' should be followed with a string value")
                    return null
                }

                jsModuleName = packageNameString
            }

            "-r" -> {
                reportPath = argsIterator.readArg()

                if (reportPath == null) {
                    printError("'-r' should be followed with a string value")
                    return null
                }

            }

            else -> when {
                arg.endsWith(TS_DECLARATION_EXTENSION) -> {
                    sources.add(arg)
                }
                arg.endsWith(IDL_DECLARATION_EXTENSION) ||
                arg.endsWith(WEBIDL_DECLARATION_EXTENSION) -> {
                    printWarning("Web IDL support is at early alpha stage and is not supposed to produce any production-quality code")
                    sources.add(arg)
                }
                else -> {
                    printError("""
following file extensions are supported:
    *.d.ts - for TypeScript declarations
    *.idl, *.webidl - [EXPERIMENTAL] for Web IDL declarations                            
                """.trimIndent())
                    return null
                }
            }
        }
    }

    return CliOptions(sources, outDir, engine, basePackageName, jsModuleName, reportPath)
}

fun main(vararg args: String) {
    if (args.isEmpty()) {
        printUsage("dukat")
        return
    }

    val options = process(args.toList())

    if (options == null) {
            exitProcess(1)
    } else {
        options.engine == Engine.GRAAL

        val moduleResolver = if (options.jsModuleName != null) {
            ConstNameResolver(IdentifierEntity(options.jsModuleName))
        } else {
            CommonJsNameResolver()
        }

        options.sources.forEach { sourceName ->
            when {
                sourceName.endsWith(TS_DECLARATION_EXTENSION) -> compile(
                        sourceName,
                        options.outDir,
                        when (options.engine) {
                            Engine.GRAAL -> createGraalTranslator(options.basePackageName, moduleResolver)
                        },
                        options.reportPath
                )
                sourceName.endsWith(IDL_DECLARATION_EXTENSION) ||
                sourceName.endsWith(WEBIDL_DECLARATION_EXTENSION) -> {
                    compile(
                            sourceName,
                            options.outDir,
                            IdlInputTranslator(),
                            options.reportPath
                    )

                }
            }
        }
    }
}


