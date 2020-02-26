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

external interface TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>

external interface EventHandlerBase<TContext, T>

typealias EventHandler<TCurrentTarget, TData> = EventHandlerBase<TCurrentTarget, TriggeredEvent<TCurrentTarget, TData, Any, Any>>

external var yargs: yargs.Argv<Any>

external interface `T$1` {
    var x: String
}

external var yarrrrgs: yargs.Arrrrgv<`T$1`>

// ------------------------------------------------------------------------------------------
@file:JsQualifier("yargs")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package yargs

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

external interface Argv<T> {
    fun ping(): T
}

external interface Arrrrgv<T> {
    fun ping(): T
}

external interface `T$0` {
    var x: Number
}

external interface Rrrrgh : Arrrrgv<`T$0`>