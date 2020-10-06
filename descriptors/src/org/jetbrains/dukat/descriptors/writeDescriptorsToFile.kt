package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.kotlin.backend.common.output.OutputFile
import org.jetbrains.kotlin.backend.common.output.SimpleOutputBinaryFile
import org.jetbrains.kotlin.backend.common.output.SimpleOutputFile
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.serialization.js.JsModuleDescriptor
import org.jetbrains.kotlin.serialization.js.JsSerializerProtocol
import org.jetbrains.kotlin.serialization.js.KotlinJavascriptSerializationUtil
import org.jetbrains.kotlin.serialization.js.ModuleKind
import org.jetbrains.kotlin.utils.JsMetadataVersion
import java.io.File
import java.io.FileOutputStream
import java.util.jar.Attributes
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.jar.Manifest


private fun SourceSetModel.getId(): String {
    return File(sources.first().fileName).nameWithoutExtension
}

private fun convertToDescriptors(sourceSet: SourceSetModel, stdLib: String): Collection<OutputFile> {
    if (sourceSet.sources.isEmpty()) {
        return emptyList()
    }

    val name = sourceSet.getId()

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

    return listOf(outputMetaJsFile) + metadata.serializedPackages().map { serializedPackage ->
        SimpleOutputBinaryFile(
                listOf(),
                JsSerializerProtocol.getKjsmFilePath(serializedPackage.fqName),
                serializedPackage.bytes
        )
    }
}

fun writeDescriptorsToJar(sourceSet: SourceSetModel, stdLib: String, outputDirPath: String): Collection<String> {
    val outputDir = File(outputDirPath)
    outputDir.mkdirs()

    if (sourceSet.sources.isEmpty()) {
        return emptyList()
    }

    val manifest = Manifest()
    val mainAttributes: Attributes = manifest.mainAttributes
    mainAttributes.putValue("Manifest-Version", "1.0")
    mainAttributes.putValue("Created-By", "JetBrains Kotlin")

    val jarName = "${sourceSet.getId()}.jar"
    val fileOutputStream = FileOutputStream(outputDir.resolve(jarName))
    val jarStream = JarOutputStream(fileOutputStream, manifest)

    convertToDescriptors(sourceSet, stdLib).forEach { outputFile ->
        jarStream.putNextEntry(JarEntry(outputFile.relativePath))
        jarStream.write(outputFile.asByteArray())
    }

    jarStream.finish()

    return listOf(jarName)
}

fun writeDescriptorsToFile(sourceSet: SourceSetModel, stdLib: String, outputDirPath: String): Collection<String> {
    val outputDir = File(outputDirPath)
    outputDir.mkdirs()

    return convertToDescriptors(sourceSet, stdLib).map { outputFile ->
        val output = outputDir.resolve(outputFile.relativePath)
        output.writeBytes(outputFile.asByteArray())
        outputFile.relativePath
    }
}