// [test] literalType.kt
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

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

external fun foo(s: String /* "number" | "string" */): dynamic /* Number | String */

external interface I {
    fun bar(s: String /* "number" | "string" */): dynamic /* Number | String */
}

external open class C {
    open fun baz(s: String /* "number" | "string" */): dynamic /* Number | String */
}

external interface Foo {
    var defaultPort: Number /* 80 */
    var primeSeed: Number? /* 2 | 3 | 5 | 7 | 11 | 13 */
        get() = definedExternally
        set(value) = definedExternally
    var floatSeed: Number /* 1.34 | 5.66 | 7.22 */
    var stringSeed: String /* "a" | "b" | "c" */
    var alphaNumeric: dynamic /* 3 | 20 | "3" | "20" */
        get() = definedExternally
        set(value) = definedExternally
    var alwaysTrue: Boolean
    fun ping(evt: String /* "show" | "hide" | "destroy" | "create" */)
    fun randomEvent(): String /* "show" | "hide" | "destroy" | "create" */
}