package org.jetbrains.dukat.translatorString

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import org.jetbrains.dukat.translator.TranslationErrorFileNotFound
import org.jetbrains.dukat.translator.TranslationErrorInvalidFile
import org.jetbrains.dukat.translator.TranslationUnitResult
import org.jetbrains.dukat.translatorString.IDL_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.StringTranslator
import org.jetbrains.dukat.translatorString.TS_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.WEBIDL_DECLARATION_EXTENSION
import java.io.File

private typealias SourceUnit = Pair<String, NameEntity>

private fun unescape(name: String): String {
    return name.replace("(?:^`)|(?:`$)".toRegex(), "")
}

fun NameEntity.translate(): String = when (this) {
    is IdentifierEntity -> value
    is QualifierEntity -> {
        if (leftMost() == ROOT_PACKAGENAME) {
            shiftLeft()!!.translate()
        } else {
            "${left.translate()}.${right.translate()}"
        }
    }
}

private fun NameEntity.fileNameFragment(): String? {
    val unprefixedName = shiftLeft()

    return if (unprefixedName == null) {
        null
    } else {
        unprefixedName.process(::unescape).translate()
    }
}

private fun SourceFileModel.resolveAsTargetName(packageName: NameEntity): String {
    val sourceFile = File(fileName)
    val sourceFileName = sourceFile.name
    val ktFileNamePrefix =
            if (sourceFileName.endsWith(TS_DECLARATION_EXTENSION)) {
                val moduleName = CommonJsNameResolver().resolveName(sourceFile)
                val sourceName = sourceFileName.removeSuffix(TS_DECLARATION_EXTENSION)
                if (moduleName != null) {
                    "$moduleName.$sourceName"
                } else {
                    sourceName
                }
            } else if (sourceFileName.endsWith(IDL_DECLARATION_EXTENSION)) {
                sourceFileName.removeSuffix(IDL_DECLARATION_EXTENSION)
            } else if (sourceFileName.endsWith(WEBIDL_DECLARATION_EXTENSION)) {
                sourceFileName.removeSuffix(WEBIDL_DECLARATION_EXTENSION)
            } else sourceFile.name

    var res = ktFileNamePrefix

    if (sourceFile.absolutePath.endsWith(TS_DECLARATION_EXTENSION)) {
        packageName.fileNameFragment()?.let { packageFragment ->
            res += ".${packageFragment}"
        }

        name?.let {
            res += ".${it}"
        }
    }

    return res
}

fun translateModule(sourceFile: SourceFileModel): List<ModuleTranslationUnit> {
    val docRoot = sourceFile.root
    return docRoot.flattenDeclarations().map { module ->
        val stringTranslator = StringTranslator()
        stringTranslator.process(module)
        ModuleTranslationUnit(sourceFile.resolveAsTargetName(module.name), sourceFile.fileName, module.name, stringTranslator.output())
    }
}

fun translateModule(sourceSet: SourceSetModel): List<TranslationUnitResult> {
    val visited = mutableSetOf<SourceUnit>()

    return sourceSet.sources.mapNotNull { sourceFile ->
        // TODO: investigate whether it's safe to check just moduleName
        val sourceKey = Pair(sourceFile.fileName, sourceFile.root.name)
        if (!visited.contains(sourceKey)) {
            visited.add(sourceKey)
            translateModule(sourceFile)
        } else {
            null
        }
    }.flatten()
}

fun translateModule(sourceBundle: SourceBundleModel): List<TranslationUnitResult> {
    return sourceBundle.sources.flatMap { sourceSet ->
        translateModule(sourceSet)
    }
}

fun translateModule(data: ByteArray, translator: InputTranslator<ByteArray>): List<TranslationUnitResult> {
    return translateModule(translator.translate(data))
}

fun translateModule(fileName: String, translator: InputTranslator<String>): List<TranslationUnitResult> {
    if (!fileName.endsWith(TS_DECLARATION_EXTENSION) &&
        !fileName.endsWith(IDL_DECLARATION_EXTENSION) &&
        !fileName.endsWith(WEBIDL_DECLARATION_EXTENSION)) {
        return listOf(TranslationErrorInvalidFile(fileName))
    }

    if (!File(fileName).exists()) {
        return listOf(TranslationErrorFileNotFound(fileName))
    }

    val sourceSet =
            translator.translate(fileName)

    return translateModule(sourceSet)
}