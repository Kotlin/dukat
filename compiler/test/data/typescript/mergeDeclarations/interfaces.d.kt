@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

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

external interface SomeNode

external interface Assignable

external interface SomeElement : SomeNode, Assignable {
    fun bong(): Boolean
    fun bing(): Boolean
    fun pong(): Boolean
    fun ping(): Boolean
}

// ------------------------------------------------------------------------------------------
@file:JsQualifier("_")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package `_`

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

external interface LoDashStatic {
    fun <T> compact(array: Array<T>): Array<T>
    fun <T> compact(array: List<T>): Array<T>
    fun chain(value: Number): LoDashWrapper<Number>
    fun chain(value: String): LoDashWrapper<String>
    fun chain(value: Boolean): LoDashWrapper<Boolean>
    fun <T> chain(value: Array<T>): LoDashArrayWrapper<T>
    fun chain(value: Any): LoDashWrapper<Any>
}

external interface LoDashWrapper<T>

external interface LoDashArrayWrapper<T>