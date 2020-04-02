package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.kotlin.backend.common.output.SimpleOutputFile
import org.jetbrains.kotlin.com.intellij.openapi.util.io.FileUtil
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.serialization.js.JsModuleDescriptor
import org.jetbrains.kotlin.serialization.js.KotlinJavascriptSerializationUtil
import org.jetbrains.kotlin.serialization.js.ModuleKind
import org.jetbrains.kotlin.utils.JsMetadataVersion
import java.io.File

fun writeDescriptorsToFile(translator: InputTranslator<ByteArray>, data: ByteArray, outputDir: String) {
    val sourceSet = translator.translate(data)
    val moduleDescriptor = sourceSet.translateToDescriptors()

    val name = File(sourceSet.sources.first().fileName).nameWithoutExtension

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
    val outputFile =
        SimpleOutputFile(listOf(), "$name.meta.js", metadata.asString())
    FileUtil.writeToFile(File(outputDir, outputFile.relativePath), outputFile.asByteArray())
}