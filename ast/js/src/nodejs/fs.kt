package org.jetbrains.dukat.ast.nodejs;

class EncodingOptions(val encoding: String = "utf8")

@JsModule("fs")
external object fs {
    fun argv(index: Int): String
    fun writeFileSync(path: String, text: String): Unit
    fun readFileSync(path: String): String
    fun readFileSync(path: String, encodingOptions: EncodingOptions): String
    fun readdirSync(path: String): Array<String>
    fun existsSync(path: String): Boolean
    fun mkdirSync(path: String): Unit
}