package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.kotlin.backend.common.output.SimpleOutputBinaryFile
import org.jetbrains.kotlin.backend.common.output.SimpleOutputFile
import org.jetbrains.kotlin.com.intellij.openapi.util.io.FileUtil
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.serialization.js.JsModuleDescriptor
import org.jetbrains.kotlin.serialization.js.JsSerializerProtocol
import org.jetbrains.kotlin.serialization.js.KotlinJavascriptSerializationUtil
import org.jetbrains.kotlin.serialization.js.ModuleKind
import org.jetbrains.kotlin.utils.JsMetadataVersion
import java.io.File

fun writeDescriptorsToFile(sourceSet: SourceSetModel, outputDir: String, stdLib: String) {
    val flattenedSourceSet = sourceSet.copy(sources = sourceSet.sources.flatMap { sourceFile ->
        sourceFile.root.flattenDeclarations().map {
            SourceFileModel(
                sourceFile.name,
                sourceFile.fileName,
                it,
                sourceFile.referencedFiles
            )
        }
    })

    val moduleDescriptor = flattenedSourceSet.translateToDescriptors(stdLib)

    val name = File(flattenedSourceSet.sources.firstOrNull()?.fileName ?: "index.d.ts").nameWithoutExtension

    val metadata = KotlinJavascriptSerializationUtil.serializeMetadata(
        BindingContext.EMPTY,
        JsModuleDescriptor(
            name,
            ModuleKind.COMMON_JS,
            listOf(),
            moduleDescriptor
        ),
        LanguageVersionSettingsImpl.DEFAULT,
        JsMetadataVersion.INSTANCE
    )
    val outputMetaJsFile =
        SimpleOutputFile(listOf(), "$name.meta.js", metadata.asString())

    val outputKjsmFiles = metadata.serializedPackages().map { serializedPackage ->
        SimpleOutputBinaryFile(
            listOf(),
            JsSerializerProtocol.getKjsmFilePath(serializedPackage.fqName),
            serializedPackage.bytes
        )
    }

    FileUtil.writeToFile(File(outputDir, outputMetaJsFile.relativePath), outputMetaJsFile.asByteArray())
    outputKjsmFiles.forEach { kjsmFile ->
        FileUtil.writeToFile(File(outputDir, kjsmFile.relativePath), kjsmFile.asByteArray())
    }
}