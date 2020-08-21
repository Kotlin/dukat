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

external interface AsyncIterable<T>

/* extending interface from lib.es2015.iterable.d.ts */
inline fun <T> Iterator<T>.next(value: Any? = definedExternally /* null */): IteratorResult<T> = this.asDynamic().next(value)

external interface AsyncIterableIterator<T>

inline var SymbolConstructor.iterator: Any get() = this.asDynamic().iterator; set(value) { this.asDynamic().iterator = value }

inline var SymbolConstructor.asyncIterator: Any get() = this.asDynamic().asyncIterator; set(value) { this.asDynamic().asyncIterator = value }

external var Symbol: SymbolConstructor

external interface SharedArrayBuffer {
    var byteLength: Number
    fun slice(begin: Number? = definedExternally /* null */, end: Number? = definedExternally /* null */): SharedArrayBuffer
}