@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
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

inline fun <T, B> Promise<T>.foo(b: B): T { return this.asDynamic().foo(b) }
inline fun <T0, T, B> Promise<T0>.foo(a: Any, b: B): T { return this.asDynamic().foo(a, b) }
inline var <T> Promise<T>.bar: Array<T> get() = this.asDynamic().bar; set(value) { this.asDynamic().bar = value }

// ------------------------------------------------------------------------------------------
@file:JsModule("ref-array")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
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

external interface ArrayType<T> {
    @nativeGetter
    operator fun get(prop: String): Number?
    @nativeSetter
    operator fun set(prop: String, value: Number)
    var someField: String
    var optionalField: T? get() = definedExternally; set(value) = definedExternally
    @nativeInvoke
    operator fun invoke(resourceId: String, hash: Any? = definedExternally /* null */, callback: Function<*>? = definedExternally /* null */)
}
