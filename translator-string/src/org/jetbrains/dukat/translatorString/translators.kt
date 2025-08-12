package org.jetbrains.dukat.translatorString

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.appendRight
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astCommon.hasPrefix
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.stdlib.KLIBROOT
import org.jetbrains.dukat.stdlib.TSLIBROOT
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import org.jetbrains.dukat.translator.TranslationErrorFileNotFound
import org.jetbrains.dukat.translator.TranslationErrorInvalidFile
import org.jetbrains.dukat.translator.TranslationUnitResult
import java.io.File

private fun unescape(name: String): String {
    return name.replace("(?:^`)|(?:`$)".toRegex(), "")
}

fun NameEntity.translate(shortNameForKotlin: Boolean = true): String = when (this) {
    is IdentifierEntity -> value
    is QualifierEntity -> {
        when {
            shortNameForKotlin && hasPrefix(KLIBROOT) -> rightMost().translate()
            hasPrefix(ROOT_PACKAGENAME) -> shiftLeft().translate()
            else -> "${left.translate(shortNameForKotlin)}.${right.translate()}"
        }
    }
}

private fun NameEntity.normalize(): NameEntity? {
    return if (hasPrefix(ROOT_PACKAGENAME)) {
        this.shiftLeft()
    } else {
        this
    }
}

private fun NameEntity.replacePrefix(oldPrefix: NameEntity, newPrefix: NameEntity, stringRemainder: NameEntity? = null, onMatch: (() -> Unit)? = null): NameEntity {
    if (this == oldPrefix) {
        onMatch?.invoke()
        return stringRemainder?.appendRight(newPrefix) ?: newPrefix
    }

    if (this is QualifierEntity) {
        return left.replacePrefix(oldPrefix, newPrefix, stringRemainder?.appendRight(right) ?: right)
    }

    return stringRemainder?.appendRight(this) ?: this
}


private fun SourceFileModel.resolveAsTargetName(packageName: NameEntity, clashMap: MutableMap<String, Int>): NameEntity {
    val sourceFile = File(fileName)
    val sourceFileName = sourceFile.name
    val ktFileNamePrefix =
            when {
                sourceFileName.endsWith(D_TS_DECLARATION_EXTENSION) -> sourceFileName.removeSuffix(D_TS_DECLARATION_EXTENSION)
                sourceFileName.endsWith(D_MTS_DECLARATION_EXTENSION) -> sourceFileName.removeSuffix(D_MTS_DECLARATION_EXTENSION)
                sourceFileName.endsWith(TS_DECLARATION_EXTENSION) -> sourceFileName.removeSuffix(TS_DECLARATION_EXTENSION)
                sourceFileName.endsWith(IDL_DECLARATION_EXTENSION) -> sourceFileName.removeSuffix(IDL_DECLARATION_EXTENSION)
                sourceFileName.endsWith(WEBIDL_DECLARATION_EXTENSION) -> sourceFileName.removeSuffix(WEBIDL_DECLARATION_EXTENSION)
                else -> sourceFileName
            }

    if (sourceFileName.endsWith(IDL_DECLARATION_EXTENSION) || sourceFileName.endsWith(WEBIDL_DECLARATION_EXTENSION)) {
        return ktFileNamePrefix.toNameEntity()
    }

    var addModuleName = true


    var name =
            packageName
                    .replacePrefix(ROOT_PACKAGENAME, ktFileNamePrefix.toNameEntity())
                    .replacePrefix(TSLIBROOT, ktFileNamePrefix.toNameEntity()) {
                        addModuleName = false
                    }
                    .process { unescape(it) }

    if (addModuleName) {
        CommonJsNameResolver().resolveName(sourceFile)?.let { moduleName ->
            val moduleNameFlattened = moduleName.replace("/", "_")
            name = name.appendLeft(IdentifierEntity("module_$moduleNameFlattened"))
        }
    }


    this.name?.let {
        name = name.appendLeft(it.normalize() ?: IdentifierEntity("_"))
    }

    val nameString = name.toString().lowercase()

    clashMap[nameString] = clashMap.getOrPut(nameString) { 0 } + 1
    clashMap[nameString]?.let { count ->
        if (count > 1) {
            name = name.appendLeft(IdentifierEntity("_${count}"))
        }
    }

    return name
}

private fun translateSourceFile(sourceFile: SourceFileModel): List<ModuleTranslationUnit> {
    val docRoot = sourceFile.root
    val clashMap: MutableMap<String, Int> = mutableMapOf()
    return docRoot.flattenDeclarations().map { module ->
        val stringTranslator = StringTranslator()
        stringTranslator.process(module)
        ModuleTranslationUnit(sourceFile.resolveAsTargetName(module.name, clashMap).translate(), sourceFile.fileName, module.name, stringTranslator.output())
    }
}

private data class TranslationKey(val fileName: String, val rootName: NameEntity, val name: NameEntity?)

fun translateSourceSet(sourceSet: SourceSetModel): List<TranslationUnitResult> {
    val visited = mutableSetOf<TranslationKey>()
    return sourceSet.sources.mapNotNull { sourceFile ->
        // TODO: investigate whether it's safe to check just moduleName

        val sourceKey = TranslationKey(sourceFile.fileName, sourceFile.root.name, sourceFile.name)
        if (!visited.contains(sourceKey)) {
            visited.add(sourceKey)
            translateSourceFile(sourceFile)
        } else {
            null
        }
    }.flatten()
}

fun translateByteArray(data: ByteArray, translator: InputTranslator<ByteArray>): List<TranslationUnitResult> {
    return translateSourceSet(translator.translate(data))
}

fun translateFile(fileName: String, translator: InputTranslator<String>): List<TranslationUnitResult> {
    if (!fileName.endsWith(TS_DECLARATION_EXTENSION) &&
        !fileName.endsWith(D_TS_DECLARATION_EXTENSION) &&
        !fileName.endsWith(D_MTS_DECLARATION_EXTENSION) &&
        !fileName.endsWith(IDL_DECLARATION_EXTENSION) &&
        !fileName.endsWith(WEBIDL_DECLARATION_EXTENSION)) {
        return listOf(TranslationErrorInvalidFile(fileName))
    }

    if (!File(fileName).exists()) {
        return listOf(TranslationErrorFileNotFound(fileName))
    }

    val sourceSet =
            translator.translate(fileName)

    return translateSourceSet(sourceSet)
}


private fun TranslationUnitResult.resolveAsError(source: String): String {
    return when (this) {
        is TranslationErrorInvalidFile -> "invalid file name: ${fileName} - only typescript declarations, that is, files with *.d.[m]ts extension can be processed"
        is TranslationErrorFileNotFound -> "file not found: ${fileName}"
        else -> "failed to translate ${source} for unknown reason"
    }
}

fun compileUnits(translatedUnits: List<TranslationUnitResult>, outDir: String): Iterable<String> {
    val dirFile = File(outDir)
    if (translatedUnits.isNotEmpty()) {
        dirFile.mkdirs()
    }

    val output = mutableListOf<String>()

    translatedUnits.forEach { translationUnitResult ->
        if (translationUnitResult is ModuleTranslationUnit) {
            val targetName = "${translationUnitResult.name}.kt"

            val resolvedTarget = dirFile.resolve(targetName)

            println(resolvedTarget.name)

            output.add(resolvedTarget.name)

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

    return output
}
