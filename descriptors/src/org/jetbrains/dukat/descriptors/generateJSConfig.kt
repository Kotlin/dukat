package org.jetbrains.dukat.descriptors

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.js.config.JsConfig
import org.jetbrains.kotlin.name.Name

fun generateCompilerConfiguration(): CompilerConfiguration {
    val configuration = CompilerConfiguration()
    configuration.put<String>(CommonConfigurationKeys.MODULE_NAME, "test-module")

    configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, object :
        MessageCollector {
        override fun clear() {}
        override fun report(
            severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?
        ) {
            if (severity == CompilerMessageSeverity.ERROR) {
                val prefix =
                    if (location == null) "" else "(" + location.path + ":" + location.line + ":" + location.column + ") "
                throw AssertionError(prefix + message)
            }
        }

        override fun hasErrors(): Boolean {
            return false
        }
    })

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