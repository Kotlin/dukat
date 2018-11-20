package org.jetbrains.dukat.ast

expect class FileResolver {
    fun resolve(fileName: String): String;
}