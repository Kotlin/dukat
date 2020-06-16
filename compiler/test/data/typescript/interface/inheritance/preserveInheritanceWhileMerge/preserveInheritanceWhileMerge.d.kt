// [test] _api.api.module_dukat-testcase.kt
@file:JsModule("api")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package api

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
import _commonapi.GeneralPlatform

external interface NativePlatform {
    fun compile(): Boolean
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface Platform<T> : NativePlatform, GeneralPlatform {
    fun ping(): T
    fun pong(): Boolean

    companion object {
        @nativeInvoke
        operator fun <T> invoke(type: String, length: Number = definedExternally): Platform<T>
    }
}

// ------------------------------------------------------------------------------------------
// [test] index._commonapi.module__commonapi.kt
@file:JsModule("_commonapi")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package _commonapi

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

external interface GeneralPlatform {
    var version: String
}