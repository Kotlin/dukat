package org.jetbrains.dukat.compiler.tests.descriptors

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.lang.Language
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.io.FileUtil
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.com.intellij.openapi.vfs.CharsetToolkit
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.com.intellij.psi.impl.PsiFileFactoryImpl
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.AnalysisFlag
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.js.analyze.TopDownAnalyzerFacadeForJS
import org.jetbrains.kotlin.js.config.JSConfigurationKeys
import org.jetbrains.kotlin.js.config.JsConfig
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

fun createFile(name: String, text: String, project: Project): KtFile {
    var shortName = name.substring(name.lastIndexOf('/') + 1)
    shortName = shortName.substring(shortName.lastIndexOf('\\') + 1)
    val virtualFile =
        LightVirtualFile(shortName, (KotlinLanguage.INSTANCE as Language), StringUtilRt.convertLineSeparators(text))

    virtualFile.charset = CharsetToolkit.UTF8_CHARSET
    val factory = PsiFileFactory.getInstance(project) as PsiFileFactoryImpl

    return factory.trySetupPsiForFile(virtualFile, (KotlinLanguage.INSTANCE as Language), true, false) as KtFile
}

fun doLoadFile(myFullDataPath: String, name: String): String {
    val fullName = myFullDataPath + File.separatorChar + name
    return doLoadFile(File(fullName))
}

fun doLoadFile(file: File): String {
    try {
        return FileUtil.loadFile(file, "UTF-8", true)
    } catch (fileNotFoundException: FileNotFoundException) {
        val messageWithFullPath = file.absolutePath + " (No such file or directory)"
        throw IOException(
            "Ensure you have your 'Working Directory' configured correctly as the root " +
                    "Kotlin project directory in your test configuration\n\t" +
                    messageWithFullPath,
            fileNotFoundException
        )
    }
}

fun analyze(
    file: KtFile,
    explicitLanguageVersion: LanguageVersion?,
    specificFeatures: Map<LanguageFeature, LanguageFeature.State>,
    environment: KotlinCoreEnvironment
): AnalysisResult {
    val configuration = environment.configuration.copy()
    configuration.put(CommonConfigurationKeys.MODULE_NAME, "test-module")
    configuration.put(JSConfigurationKeys.LIBRARIES, JsConfig.JS_STDLIB)
    configuration.put(CommonConfigurationKeys.DISABLE_INLINE, true)

    val languageVersion = explicitLanguageVersion ?: configuration.languageVersionSettings.languageVersion

    configuration.languageVersionSettings = LanguageVersionSettingsImpl(
        languageVersion,
        LanguageVersionSettingsImpl.DEFAULT.apiVersion,
        emptyMap<AnalysisFlag<*>, Any>(),
        specificFeatures
    )
    val config = JsConfig(environment.project, configuration)

    return TopDownAnalyzerFacadeForJS.analyzeFiles(listOf(file), config)
}

fun generatePackageDescriptor(filePath: String, fileName: String): PackageViewDescriptor {
    val environment = KotlinCoreEnvironment.createForTests(
        Disposable { },
        CompilerConfiguration(),
        EnvironmentConfigFiles.JS_CONFIG_FILES
    )
    val project = environment.project
    val psiFile = createFile(fileName, doLoadFile(filePath, fileName), project)
    val moduleDescriptor = analyze(psiFile, null, mapOf(), environment).moduleDescriptor
    return moduleDescriptor.getPackage(psiFile.packageFqName)
}
