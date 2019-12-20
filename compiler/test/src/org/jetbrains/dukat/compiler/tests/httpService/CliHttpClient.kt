package org.jetbrains.dukat.compiler.tests.httpService

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Serializable
private data class TranslationRequest(
        val packageName: String,
        val files: List<String>
)

class CliHttpClient(private val port: String) {

    private fun pingStatus(): Int? {
        val url = URL("http://localhost:${port}/status")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        return try {
            connection.responseCode
        } catch (e: Exception) {
            null
        }
    }

    fun waitForServer() {
        var count = 0
        do {
            Thread.sleep(250)
            count++
        } while (pingStatus() != 200 || count > 10)
    }

    fun translate(fileName: String): ByteArray {
        val url = URL("http://localhost:${port}/dukat")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 10000

        val messageRaw = Json.nonstrict.toJson(TranslationRequest.serializer(), TranslationRequest(
                packageName = "<ROOT>",
                files = listOf(fileName)
        )).toString()
        val postBody = messageRaw.toByteArray()

        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Content-length", postBody.size.toString())
        connection.doOutput = true

        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.write(postBody)
        outputStream.flush()

        return connection.inputStream.readBytes()
    }
}

fun main() {
    CliHttpClient("8090").waitForServer()
}