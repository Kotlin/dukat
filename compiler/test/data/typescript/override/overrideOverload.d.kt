// [test] overrideOverload.API.kt
@file:JsQualifier("API")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package API

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

external interface EmitterA

external interface EmitterB

external interface Domain {
    fun add(emitter: EmitterA)
    fun add(emitter: EmitterB)
    fun remove(emitter: EmitterA)
    fun remove(emitter: EmitterB)
}

external open class SubDomain : Domain {
    override fun add(emitter: EmitterA)
    override fun add(emitter: EmitterB)
    override fun remove(emitter: EmitterA)
    override fun remove(emitter: EmitterB)
}