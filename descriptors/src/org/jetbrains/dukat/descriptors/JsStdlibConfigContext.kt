package org.jetbrains.dukat.descriptors

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.js.config.JSConfigurationKeys
import org.jetbrains.kotlin.js.config.JsConfig
import org.jetbrains.kotlin.name.Name

class JsStdlibConfigContext {

    private val disposable = Disposer.newDisposable()
    val environment = generateDefaultEnvironment()
    val stdlibModule = deserializeStdlib()

    fun destroy() {
        Disposer.dispose(disposable)
    }

    private fun generateCompilerConfiguration(): CompilerConfiguration {
        val configuration = CompilerConfiguration()
        configuration.put(CommonConfigurationKeys.MODULE_NAME, "test-module")
        configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        configuration.put(JSConfigurationKeys.LIBRARIES, JsConfig.JS_STDLIB)

        return configuration
    }


    private fun generateDefaultEnvironment(): KotlinCoreEnvironment {
        return KotlinCoreEnvironment.createForProduction(
            disposable,
            generateCompilerConfiguration(),
            EnvironmentConfigFiles.JS_CONFIG_FILES
        )
    }

    fun generateJSConfig(): JsConfig {
        return JsConfig(environment.project, environment.configuration)
    }

    private fun deserializeStdlib(): ModuleDescriptor {
        return generateJSConfig().moduleDescriptors.first { it.name == Name.special("<kotlin>") }
    }
}