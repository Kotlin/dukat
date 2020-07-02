package org.jetbrains.dukat.compiler.tests.httpService

import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jetbrains.dukat.compiler.tests.core.TestConfig
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Serializable
private data class TranslationRequest(
        val packageName: String,
        val files: List<String>,
        val tsConfig: String?
)

class CliHttpClient(private val port: String) {

    private fun pingStatus(): Int? {
        val url = URL("http://localhost:${port}/status")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        return connection.responseCode
    }

    suspend fun waitForServer(): Boolean {
        var count = 0
        while (true) {
            println("connecting, attempt ${count++} ")
            try {
                val status = pingStatus()
                if (status == 200) {
                    return true
                }
            } catch (e: Exception) {
                if (count > 10) {
                    throw e
                }

                delay(250)
            }
        }
    }

    fun translate(fileName: String, tsConfig: String?): ByteArray {
        val url = URL("http://localhost:${port}/dukat")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 10000

        val messageRaw = Json(JsonConfiguration.Stable.copy(prettyPrint = true, ignoreUnknownKeys = true)).toJson(TranslationRequest.serializer(), TranslationRequest(
                packageName = "<ROOT>",
                files = listOf(fileName),
                tsConfig = tsConfig
        )).toString()
        val postBody = messageRaw.toByteArray()

        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Content-length", postBody.size.toString())
        connection.doOutput = true

        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.write(postBody)
        outputStream.flush()
        outputStream.close()

        val result = connection.inputStream.readBytes()

        connection.inputStream.close()
        connection.disconnect()

        return result
    }
}

suspend fun main() {
    CliHttpClient(TestConfig.CLI_TEST_SERVER_PORT).waitForServer()
}