package org.jetbrains.dukat.interop

interface InteropEngine {
    fun eval(script: String)
    fun <T> callFunction(name: String, vararg params: Any?): T
}