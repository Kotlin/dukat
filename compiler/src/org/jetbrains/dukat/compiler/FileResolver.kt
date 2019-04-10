package org.jetbrains.dukat.compiler

import java.io.File


class FileResolver {

    private val myFileCache: MutableMap<String, String> = mutableMapOf()

    fun readFile(fileName: String): String {
        return File(fileName).bufferedReader().use { it.readText() }
    }

    fun readResource(name: String): String {
        val resourceAsStream = this::class.java.classLoader.getResourceAsStream(name)
        return resourceAsStream.bufferedReader().readText()
    }

    fun resolve(fileName: String): String {
        return try {
            readFile(fileName)
        } catch (e: Exception) {
            val resourceName = "ts/${fileName}"

            myFileCache.getOrPut(resourceName) {
                readResource(resourceName)
            }
        }
    }
}