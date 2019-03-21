package org.jetbrains.dukat.nashorn.interop

import org.jetbrains.dukat.interop.InteropEngine
import javax.script.Invocable
import javax.script.ScriptEngineManager

class InteropNashorn : InteropEngine {

    private val myEngine =  ScriptEngineManager().getEngineByName("nashorn")

    override fun eval(script: String) {
        myEngine.eval(script)
    }

    fun put(key: String, value: Any) {
        myEngine.put(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> callFunction(name: String, vararg params: Any?): T {
        val invocable = myEngine as Invocable
        return invocable.invokeFunction(name, *params) as T
    }

    override fun release() {}
}