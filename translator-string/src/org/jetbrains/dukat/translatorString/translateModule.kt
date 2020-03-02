package org.jetbrains.dukat.translatorString

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translator.LIB_PACKAGENAME
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import org.jetbrains.dukat.translator.TranslationErrorFileNotFound
import org.jetbrains.dukat.translator.TranslationErrorInvalidFile
import org.jetbrains.dukat.translator.TranslationUnitResult
import java.io.File

private fun unescape(name: String): String {
    return name.replace("(?:^`)|(?:`$)".toRegex(), "")
}

fun NameEntity.translate(): String = when (this) {
    is IdentifierEntity -> value
    is QualifierEntity -> {
        when (leftMost()) {
            ROOT_PACKAGENAME -> shiftLeft()!!.translate()
            LIB_PACKAGENAME -> shiftLeft()!!.translate()
            else -> "${left.translate()}.${right.translate()}"
        }
    }
}

private fun NameEntity.normalize(): NameEntity? {
    return if (leftMost() == ROOT_PACKAGENAME) {
        this.shiftLeft()
    } else {
        this
    }
}


private fun SourceFileModel.resolveAsTargetName(packageName: NameEntity, clashMap: MutableMap<String, Int>): NameEntity {
    val sourceFile = File(fileName)
    val sourceFileName = sourceFile.name
    val ktFileNamePrefix =
            when {
                sourceFileName.endsWith(D_TS_DECLARATION_EXTENSION) -> sourceFileName.removeSuffix(D_TS_DECLARATION_EXTENSION)
                sourceFileName.endsWith(TS_DECLARATION_EXTENSION) -> sourceFileName.removeSuffix(TS_DECLARATION_EXTENSION)
                sourceFileName.endsWith(IDL_DECLARATION_EXTENSION) -> sourceFileName.removeSuffix(IDL_DECLARATION_EXTENSION)
                sourceFileName.endsWith(WEBIDL_DECLARATION_EXTENSION) -> sourceFileName.removeSuffix(WEBIDL_DECLARATION_EXTENSION)
                else -> sourceFileName
            }

    if (sourceFileName.endsWith(IDL_DECLARATION_EXTENSION) || sourceFileName.endsWith(WEBIDL_DECLARATION_EXTENSION)) {
        return ktFileNamePrefix.toNameEntity()
    }

    var addModuleName = true

    var name = packageName.process {
        if (it == ROOT_PACKAGENAME.value) {
            ktFileNamePrefix
        } else if (it == LIB_PACKAGENAME.value) {
            addModuleName = false
            ktFileNamePrefix
        } else {
            unescape(it)
        }
    }

    if (addModuleName) {
        CommonJsNameResolver().resolveName(sourceFile)?.let { moduleName ->
            val moduleNameFlattened = moduleName.replace("/", "_")
            name = name.appendLeft(IdentifierEntity("module_$moduleNameFlattened"))
        }
    }


    this.name?.let {
        name = name.appendLeft(it.normalize() ?: IdentifierEntity("_"))
    }

    val nameString = name.toString().toLowerCase()

    clashMap[nameString] = clashMap.getOrPut(nameString) { 0 } + 1
    clashMap[nameString]?.let { count ->
        if (count > 1) {
            name = name.appendLeft(IdentifierEntity("_${count}"))
        }
    }

    return name
}

fun translateModule(sourceFile: SourceFileModel): List<ModuleTranslationUnit> {
    val docRoot = sourceFile.root
    val clashMap: MutableMap<String, Int> = mutableMapOf()
    return docRoot.flattenDeclarations().map { module ->
        val stringTranslator = StringTranslator()
        stringTranslator.process(module)
        ModuleTranslationUnit(sourceFile.resolveAsTargetName(module.name, clashMap).translate(), sourceFile.fileName, module.name, stringTranslator.output())
    }
}

private data class TranslationKey(val fileName: String, val rootName: NameEntity, val name: NameEntity?)

fun translateModule(sourceSet: SourceSetModel): List<TranslationUnitResult> {
    val visited = mutableSetOf<TranslationKey>()

    return sourceSet.sources.mapNotNull { sourceFile ->
        // TODO: investigate whether it's safe to check just moduleName
        val sourceKey = TranslationKey(sourceFile.fileName, sourceFile.root.name, sourceFile.name)
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
        !fileName.endsWith(D_TS_DECLARATION_EXTENSION) &&
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