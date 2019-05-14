package org.jetbrains.dukat.j2v8.interop

import com.eclipsesource.v8.Releasable
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import org.jetbrains.dukat.interop.InteropEngine
import org.jetbrains.dukat.panic.raiseConcern


class InteropV8(val runtime: V8 = V8.createV8Runtime("global")): Releasable, InteropEngine {

    private var myRegisteredItems = mutableListOf<Releasable>()

    private fun register(item: Releasable) {
        myRegisteredItems.add(item)
    }

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
            else -> raiseConcern("can not cast ${it}") { params.pushNull() }
        }}

        register(params)
        return params
    }

    fun executeScript(script: String): V8Object {
        val res = runtime.executeObjectScript(script)
        register(res)
        return res
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> callFunction(name: String, vararg params: Any?): T {
        val result = runtime.executeFunction(name, createParams(*params)) as T

        if (result is Releasable) {
            register(result)
        }
        return result
    }

    fun proxy(context: V8Object, proxyObject: Any)  = InteropV8Class(context, proxyObject)

    fun proxy(proxyObject: Any) = proxy(runtime, proxyObject)

    override fun release() {
        myRegisteredItems.map(Releasable::release)
        runtime.release()
    }
}