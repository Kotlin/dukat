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

external fun frequencies(a: Array<String>): Array<Number>

external interface `T$0` {
    @nativeGetter
    operator fun get(shortName: String): dynamic /* String | Array<String> */
    @nativeSetter
    operator fun set(shortName: String, value: String)
    @nativeSetter
    operator fun set(shortName: String, value: Array<String>)
}

external interface Processor<T> {
    fun process(arg: T)
    fun process(arg: Array<T>)
    fun alias(aliases: `T$0`)
}