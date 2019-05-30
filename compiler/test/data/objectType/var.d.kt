@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")

import kotlin.js.*
import kotlin.js.Json
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

external interface `T$0` {
    var bar: Number
}
external object foo {
    fun bar(a: Any): Number = definedExternally
    var baz: Any = definedExternally
    var boo: String = definedExternally
    var show: (overrideChecks: Boolean) -> Unit = definedExternally
    @nativeInvoke
    operator fun invoke(foo: `T$0`): Any = definedExternally
    @nativeGetter
    operator fun get(s: String): Any? = definedExternally
    @nativeSetter
    operator fun set(s: String, value: Any): Unit = definedExternally
}