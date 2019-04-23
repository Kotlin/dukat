package org.jetbrains.dukat.interop.graal

import org.graalvm.polyglot.Context
import org.jetbrains.dukat.interop.InteropEngine


class InteropGraal : InteropEngine {
    private val myContext =
            Context
                    .newBuilder("js")
                    .allowHostAccess(true)
                    .build()

    private val myBindings = myContext.getBindings("js")

    override fun eval(script: String) {
        myContext.eval("js", script)
    }

    fun put(key: String, value: Any) {
        myBindings.putMember(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> callFunction(name: String, vararg params: Any?): T {
        return myBindings
                .getMember(name)
                .execute(*params).asHostObject()
    }

    override fun release() {}
}