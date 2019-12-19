package org.jetbrains.dukat.cli

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.compiler.translator.IdlInputTranslator
import org.jetbrains.dukat.idlReferenceResolver.DirectoryReferencesResolver
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
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
import org.jetbrains.dukat.translatorString.translateModule
import org.jetbrains.dukat.ts.translator.createJsByteArrayTranslator
import java.io.File
import kotlin.system.exitProcess


val PACKAGE_DIR = System.getProperty("dukat.cli.internal.packagedir")

@Serializable
private data class Report(val outputs: List<String>)

private fun TranslationUnitResult.resolveAsError(source: String): String {
    return when (this) {
        is TranslationErrorInvalidFile -> "invalid file name: ${fileName} - only typescript declarations, that is, files with *.d.ts extension can be processed"
        is TranslationErrorFileNotFound -> "file not found: ${fileName}"
        else -> "failed to translate ${source} for unknown reason"
    }
}

fun translateBinaryBundle(input: ByteArray, outDir: String?, moduleNameResolver: ModuleNameResolver, pathToReport: String?) {
    val translator = createJsByteArrayTranslator(moduleNameResolver)
    val translatedUnits = translateModule(input, translator)
    compileUnits(translatedUnits, outDir, pathToReport)
}

private fun compile(filenames: List<String>, outDir: String?, translator: InputTranslator<String>, pathToReport: String?) {
    val translatedUnits: List<TranslationUnitResult> =  filenames.flatMap { filename ->
        val sourceFile = File(filename)

        translateModule(sourceFile.absolutePath, translator)
    }

    compileUnits(translatedUnits, outDir, pathToReport)
}

fun compileUnits(translatedUnits: List<TranslationUnitResult>, outDir: String?, pathToReport: String?) {
    val dirFile = File(outDir ?: "./")
    if (translatedUnits.isNotEmpty()) {
        dirFile.mkdirs()
    }

    val buildReport = pathToReport !== null

    val output = mutableListOf<String>()

    translatedUnits.forEach { translationUnitResult ->
        if (translationUnitResult is ModuleTranslationUnit) {
            val targetName = "${translationUnitResult.name}.kt"

            val resolvedTarget = dirFile.resolve(targetName)

            println(resolvedTarget.name)

            if (buildReport) {
                output.add(resolvedTarget.name)
            }

            resolvedTarget.writeText(translationUnitResult.content)
        } else {
            val fileName = when (translationUnitResult) {
                is TranslationErrorInvalidFile -> translationUnitResult.fileName
                is TranslationErrorFileNotFound -> translationUnitResult.fileName
                is ModuleTranslationUnit -> translationUnitResult.fileName
            }
            println("ERROR: ${translationUnitResult.resolveAsError(fileName)}")
        }
    }

    if (buildReport) {
        saveReport(pathToReport!!, Report(output))
    }
}

@UseExperimental(kotlinx.serialization.UnstableDefault::class)
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


private data class CliOptions(
        val sources: List<String>,
        val outDir: String?,
        val basePackageName: NameEntity,
        val jsModuleName: String?,
        val reportPath: String?,
        val tsDefaultLib: String
)


private fun printError(message: String) {
    System.err.println(message)
}

private fun process(args: List<String>): CliOptions? {
    val argsIterator = args.iterator()

    val sources = mutableListOf<String>()
    var outDir: String? = null
    var basePackageName: NameEntity = ROOT_PACKAGENAME
    var jsModuleName: String? = null
    var reportPath: String? = null
    while (argsIterator.hasNext()) {
        val arg = argsIterator.next()
        when (arg) {
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
                arg.equals("-") -> sources.add("-")
                arg.endsWith(TS_DECLARATION_EXTENSION) -> {
                    sources.add(arg)
                }
                arg.endsWith(IDL_DECLARATION_EXTENSION) ||
                        arg.endsWith(WEBIDL_DECLARATION_EXTENSION) -> {
                    sources.add(arg)
                }
                else -> {
                    printError("""
following file extensions are supported:
    *.d.ts - for TypeScript declarations
    *.idl, *.webidl - for Web IDL declarations                            
                """.trimIndent())
                    return null
                }
            }
        }
    }

    val tsDefaultLib = File(PACKAGE_DIR, "d.ts.libs/lib.d.ts").absolutePath;

    return CliOptions(sources, outDir, basePackageName, jsModuleName, reportPath, tsDefaultLib)
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

        val isTsTranslation = options.sources.all { it.endsWith(TS_DECLARATION_EXTENSION) }
        val isIdlTranslation = options.sources.all { it.endsWith(IDL_DECLARATION_EXTENSION) || it.endsWith(WEBIDL_DECLARATION_EXTENSION) }

        when {
            isTsTranslation -> {
                translateBinaryBundle(
                        System.`in`.readBytes(),
                        options.outDir,
                        moduleResolver,
                        options.reportPath
                )
            }

            isIdlTranslation-> {
                compile(
                        options.sources,
                        options.outDir,
                        IdlInputTranslator(DirectoryReferencesResolver()),
                        options.reportPath
                )
            }

            else -> {
                printError("in a single pass you can either pass only *.d.ts files or *.idl files")
                exitProcess(1)
            }
        }
    }
}


