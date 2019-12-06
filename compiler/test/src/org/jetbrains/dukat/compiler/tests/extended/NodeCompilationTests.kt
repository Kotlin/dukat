package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.core.TestConfig.TOPN_DIR
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class NodeCompilationTests : CompilationTests() {

    @DisplayName("core test set compile")
    @ParameterizedTest(name = "{0}")
    @MethodSource("extendedSet")
    @EnabledIfSystemProperty(named = "dukat.test.extended", matches = "true")
    override fun runTests(
            descriptor: String,
            sourcePath: String
    ) {
        assertContentCompiles(descriptor, sourcePath)
    }

    companion object {

        @JvmStatic
        fun extendedSet(): Array<Array<String>> {

            return listOf(
                    "@types/node/path.d.ts",
                    "@types/node/constants.d.ts",
                    "@types/node/domain.d.ts",
                    "@types/node/globals.d.ts",
                    "@types/node/string_decoder.d.ts",
                    "@types/node/tls.d.ts",
                    "@types/node/tty.d.ts",
                    "@types/node/punycode.d.ts",
                    "@types/node/readline.d.ts",
                    "@types/node/crypto.d.ts",
                    "@types/node/trace_events.d.ts",
                    "@types/node/events.d.ts",
                    "@types/node/os.d.ts",
                    "@types/node/buffer.d.ts",
                    "@types/node/querystring.d.ts",
                    "@types/node/worker_threads.d.ts",
                    "@types/node/console.d.ts",
                    "@types/node/async_hooks.d.ts",
                    "@types/node/dns.d.ts",
                    "@types/node/vm.d.ts",
                    "@types/node/timers.d.ts",
                    "@types/node/http.d.ts",
                    "@types/node/http2.d.ts",
                    "@types/node/stream.d.ts",
                    "@types/node/inspector.d.ts",
                    "@types/node/v8.d.ts",
                    "@types/node/perf_hooks.d.ts",
                    "@types/node/url.d.ts",
                    "@types/node/cluster.d.ts",
                    "@types/node/https.d.ts",
                    "@types/node/assert.d.ts",
                    "@types/node/fs.d.ts",
                    "@types/node/repl.d.ts",
                    "@types/node/dgram.d.ts",
                    "@types/node/child_process.d.ts",
                    "@types/node/zlib.d.ts",
                    "@types/node/module.d.ts",
                    "@types/node/base.d.ts",
                    "@types/node/process.d.ts",
                    "@types/node/util.d.ts",
                    "@types/node/index.d.ts",
                    "@types/node/net.d.ts"
            ).map { descriptor ->
                arrayOf(
                        descriptor,
                        File("$TOPN_DIR/node_modules/$descriptor").normalize().absolutePath
                )
            }.toTypedArray()

        }

    }
}