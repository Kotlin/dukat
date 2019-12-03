@file:JsQualifier("Q")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package Q

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

/* extending interface from Q.d.ts */
inline fun <T, B> Promise<T>.foo(b: B): T = this.asDynamic().foo(b)

/* extending interface from Q.d.ts */
inline fun <T0, T, B> Promise<T0>.foo(a: Any, b: B): T = this.asDynamic().foo(a, b)

inline var <T> Promise<T>.bar: Array<T> get() = this.asDynamic().bar; set(value) { this.asDynamic().bar = value }

// ------------------------------------------------------------------------------------------
@file:JsModule("ref-array")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package ref_array

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
import ref.Type
import Buffer

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface ArrayType<T> : Type {
    var BYTES_PER_ELEMENT: Number
    var fixedLength: Number
    var type: Type
    fun untilZeros(buffer: Buffer): `T$0`<T>
    @nativeInvoke
    operator fun invoke(length: Number? = definedExternally): `T$0`<T>
    @nativeInvoke
    operator fun invoke(data: Array<Number>, length: Number? = definedExternally): `T$0`<T>
    @nativeInvoke
    operator fun invoke(data: Buffer, length: Number? = definedExternally): `T$0`<T>
    @nativeGetter
    operator fun get(prop: String): Number?
    @nativeSetter
    operator fun set(prop: String, value: Number)
    var someField: String
    var optionalField: T?
        get() = definedExternally
        set(value) = definedExternally
    @nativeInvoke
    operator fun invoke(resourceId: String, hash: Any? = definedExternally, callback: Function<*>? = definedExternally)
}