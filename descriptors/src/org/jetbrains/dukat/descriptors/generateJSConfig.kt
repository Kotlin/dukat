package org.jetbrains.dukat.descriptors

import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.config.AnalysisFlag
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.js.config.JSConfigurationKeys
import org.jetbrains.kotlin.js.config.JsConfig

fun generateDefaultEnvironment(): KotlinCoreEnvironment {
    return KotlinCoreEnvironment.createForTests(
        Disposable { },
        CompilerConfiguration(),
        EnvironmentConfigFiles.JS_CONFIG_FILES
    )
}

fun generateJSConfig(): JsConfig {
    val environment = generateDefaultEnvironment()
    val configuration = environment.configuration.copy()
    configuration.put(CommonConfigurationKeys.MODULE_NAME, "test-module")
    configuration.put(JSConfigurationKeys.LIBRARIES, JsConfig.JS_STDLIB)
    configuration.put(CommonConfigurationKeys.DISABLE_INLINE, true)

    val languageVersion = configuration.languageVersionSettings.languageVersion

    configuration.languageVersionSettings = LanguageVersionSettingsImpl(
        languageVersion,
        LanguageVersionSettingsImpl.DEFAULT.apiVersion,
        emptyMap<AnalysisFlag<*>, Any>(),
        mapOf()
    )
    return JsConfig(environment.project, configuration)
}