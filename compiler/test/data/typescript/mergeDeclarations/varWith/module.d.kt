// [test] module.kt
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

@JsModule("lodash")
external val `_`: `_`.LoDashStatic

// ------------------------------------------------------------------------------------------
// [test] module._.kt
@file:JsModule("lodash")
@file:JsNonModule
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
    @nativeInvoke
    operator fun invoke(value: Number): LoDashArrayWrapper<Number>
    var VERSION: String
    var support: Support
}

external interface Support {
    var argsClass: Boolean
    var argsObject: Boolean
}

external interface LoDashArrayWrapper<T> {
    fun difference(vararg others: Array<T>): LoDashArrayWrapper<T>
    fun difference(vararg others: List<T>): LoDashArrayWrapper<T>
}