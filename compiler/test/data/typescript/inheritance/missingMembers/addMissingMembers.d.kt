// [test] addMissingMembers.kt
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface OptionsA

external interface OptionsB : OptionsA

external interface MyWritableStream {
    fun ping(id: Any)
    fun setOptions(options: OptionsA)
    fun write(str: String, encoding: String = definedExternally, cb: (err: Error?) -> Unit = definedExternally): Boolean
}

external open class MyStreamWritable : MyWritableStream {
    open fun ping(id: String)
    override fun ping(id: Any)
    open fun setOptions(options: OptionsB)
    override fun setOptions(options: OptionsA)
    open fun write(chunk: Any, encoding: String, cb: (error: Error?) -> Unit = definedExternally): Boolean
    override fun write(str: String, encoding: String, cb: (err: Error?) -> Unit): Boolean
}

external open class MyAbstractWritableStream {
    open fun ping(id: Any)
}

external open class MyAbstractStreamWritable : MyAbstractWritableStream