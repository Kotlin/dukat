package org.jetbrains.dukat.cli

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.compiler.translator.translateIdlSources
import org.jetbrains.dukat.descriptors.writeDescriptorsToFile
import org.jetbrains.dukat.js.translator.translateJsSources
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.panic.PanicMode
import org.jetbrains.dukat.panic.setPanicMode
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translator.TranslationErrorFileNotFound
import org.jetbrains.dukat.translator.TranslationErrorInvalidFile
import org.jetbrains.dukat.translator.TranslationUnitResult
import org.jetbrains.dukat.translatorString.D_TS_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.IDL_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.JS_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.TS_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.WEBIDL_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.compileUnits
import org.jetbrains.dukat.translatorString.translateSourceSet
import org.jetbrains.dukat.ts.translator.translateTypescriptDeclarations
import org.jetbrains.dukat.ts.translator.translateTypescriptSources
import java.io.File
import kotlin.system.exitProcess


val PACKAGE_DIR = System.getProperty("dukat.cli.internal.packagedir")

@Serializable
private data class Report(val outputs: Iterable<String>)

private fun saveReport(reportPath: String, report: Report): Boolean {
    val reportFile = File(reportPath)

    val reportBody = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }.encodeToString(Report.serializer(), report)

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

private fun printUsage(program: String) {
    println(
            """
Usage: $program [<options>] <d.ts files>

where possible options include:
    -p  <qualifiedPackageName>      package name for the generated file (by default filename.d.ts renamed to filename.d.kt)
    -m  String                      use this value as @file:JsModule annotation value whenever such annotation occurs
    -d  <path>                      destination directory for files with converted declarations (by default declarations are generated in current directory)
    -v, -version                    print version
""".trimIndent()
    )
}


private data class CliOptions(
        val sources: List<String>,
        val outDir: String,
        val basePackageName: NameEntity?,
        val jsModuleName: String?,
        val reportPath: String?,
        val tsDefaultLib: String,
        val generateDescriptors: Boolean,
        val tsConfig: String?
)


private fun printError(message: String) {
    System.err.println(message)
}

private fun process(args: List<String>): CliOptions? {
    val argsIterator = args.iterator()

    val sources = mutableListOf<String>()
    var outDir: String? = null
    var basePackageName: NameEntity? = null
    var jsModuleName: String? = null
    var reportPath: String? = null
    var generateDescriptors = false
    var tsConfig: String? = null

    while (argsIterator.hasNext()) {
        val arg = argsIterator.next()
        when (arg) {
            "--always-fail" -> {
                setPanicMode(PanicMode.ALWAYS_FAIL)
            }
            "--descriptors" -> {
                generateDescriptors = true
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
            "--ts-config" -> {
                tsConfig = argsIterator.readArg()

                if (tsConfig == null) {
                    printError("'--ts-config' should be followed with a json config")
                    return null
                }
            }

            else -> when {
                arg == "-" -> sources.add("-")
                arg.endsWith(D_TS_DECLARATION_EXTENSION) || arg.endsWith(TS_DECLARATION_EXTENSION) -> {
                    sources.add(arg)
                }
                arg.endsWith(JS_DECLARATION_EXTENSION) -> {
                    sources.add(arg)
                }
                arg.endsWith(IDL_DECLARATION_EXTENSION) ||
                        arg.endsWith(WEBIDL_DECLARATION_EXTENSION) -> {
                    sources.add(arg)
                }
                else -> {
                    printError(
                            """
following file extensions are supported:
    *.d.ts - for TypeScript declarations
    *.idl, *.webidl - for Web IDL declarations                            
                """.trimIndent()
                    )
                    return null
                }
            }
        }
    }

    val tsDefaultLib = File(PACKAGE_DIR, "d.ts.libs/lib.d.ts").absolutePath

    return CliOptions(sources, outDir
            ?: "./", basePackageName, jsModuleName, reportPath, tsDefaultLib, generateDescriptors, tsConfig)
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
        val moduleResolver = if (options.jsModuleName != null) {
            ConstNameResolver(options.jsModuleName)
        } else {
            CommonJsNameResolver()
        }

        if (options.sources.isEmpty()) {
            printError("please, specify at least one input source")
            exitProcess(1)
        }

        val isTypescriptDeclarationTranslation = options.sources.all { it.endsWith(D_TS_DECLARATION_EXTENSION) }
        val isTypescriptTranslation = options.sources.all { it.endsWith(TS_DECLARATION_EXTENSION) }
        val isJavascriptTranslation = options.sources.all { it.endsWith(JS_DECLARATION_EXTENSION) }
        val isIdlTranslation =
                options.sources.all { it.endsWith(IDL_DECLARATION_EXTENSION) || it.endsWith(WEBIDL_DECLARATION_EXTENSION) }

        val sourceSet = when {
            isTypescriptDeclarationTranslation -> {
                val stdLibPath = File(PACKAGE_DIR, "resources/stdlib.dukat").absolutePath
                translateTypescriptDeclarations(System.`in`.readBytes(), moduleResolver, options.basePackageName, true, stdLibPath)
            }

            isJavascriptTranslation -> {
                translateJsSources(System.`in`.readBytes(), moduleResolver)
            }

            isTypescriptTranslation -> {
                translateTypescriptSources(System.`in`.readBytes(), moduleResolver, options.basePackageName)
            }

            isIdlTranslation -> {
                translateIdlSources(options.sources)
            }

            else -> {
                printError("in a single pass you can either pass only *.d.ts files or *.idl files")
                exitProcess(1)
            }
        }

        if (options.generateDescriptors) {
            writeDescriptorsToFile(
                sourceSet,
                options.outDir,
                File(PACKAGE_DIR, "/build/runtime/kotlin-stdlib-js.jar").absolutePath
            ).forEach { output ->
                println(output)
            }
        } else {
            val reportOutput = compileUnits(translateSourceSet(sourceSet), options.outDir)
            if (options.reportPath != null) {
                saveReport(options.reportPath, Report(reportOutput))
            }
        }
    }
}


