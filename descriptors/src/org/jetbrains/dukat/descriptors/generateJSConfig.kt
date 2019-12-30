package org.jetbrains.dukat.descriptors

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.js.config.JSConfigurationKeys
import org.jetbrains.kotlin.js.config.JsConfig
import org.jetbrains.kotlin.name.Name

fun generateCompilerConfiguration(): CompilerConfiguration {
    val configuration = CompilerConfiguration()
    configuration.put<String>(CommonConfigurationKeys.MODULE_NAME, "test-module")
    configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
    configuration.put(JSConfigurationKeys.LIBRARIES, JsConfig.JS_STDLIB)

    return configuration
}


fun generateDefaultEnvironment(): KotlinCoreEnvironment {
    return KotlinCoreEnvironment.createForTests(
        Disposable { },
        generateCompilerConfiguration(),
        EnvironmentConfigFiles.JS_CONFIG_FILES
    )
}

fun generateJSConfig(): JsConfig {
    val environment = generateDefaultEnvironment()
    return JsConfig(environment.project, environment.configuration)
}

fun deserializeStdlib(): ModuleDescriptor {
    return generateJSConfig().moduleDescriptors.first { it.name == Name.special("<kotlin>") }
}