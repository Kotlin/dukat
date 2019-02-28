package org.jetbrains.dukat.j2v8.interop

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import org.jetbrains.dukat.interop.InteropEngine


class InteropV8(val runtime: V8 = V8.createV8Runtime("global")): InteropEngine {


    override fun eval(script: String) = runtime.executeVoidScript(script)

    private fun createParams(vararg p: Any?): V8Array {
        val params = V8Array(runtime)

        p.forEach {when {
            it == null -> params.pushNull()
            it is Int -> params.push(it)
            it is Double -> params.push(it)
            it is String -> params.push(it)
            it is Boolean -> params.push(it)
            it is V8Object -> params.push(it)
            else -> throw Exception("can not cast ${it}")
        }}

        return params
    }

    fun executeScript(script: String): V8Object {
        val res = runtime.executeObjectScript(script)
        return res
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> callFunction(name: String, vararg params: Any?): T {
        val result = runtime.executeFunction(name, createParams(*params)) as T
        return result
    }

    fun proxy(context: V8Object, proxyObject: Any)  = InteropV8Class(context, proxyObject)

    fun proxy(proxyObject: Any) = proxy(runtime, proxyObject)
}