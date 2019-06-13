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
    fun bar(a: Any): Number
    var baz: Any
    var boo: String
    var show: (overrideChecks: Boolean) -> Unit
    @nativeGetter
    operator fun get(s: String): Any?
    @nativeSetter
    operator fun set(s: String, value: Any)
}
external fun foo(): `T$0`
external fun bar(): `T$0`
external interface `T$1` {
    fun bar(a: Any): Number
    var baz: Any
    var boo: String
    var show: (flag: Boolean) -> Unit
    @nativeGetter
    operator fun get(s: String): Number?
    @nativeSetter
    operator fun set(s: String, value: Number)
}
external fun baz(): `T$1`
external interface Interface {
    var bar: `T$0`
    var baz: `T$1`
}
