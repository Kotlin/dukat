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

/* extending interface from lib.dom.d.ts */
inline fun Event.foo() { this.asDynamic().foo() }

inline var Event.bar: Any get() = this.asDynamic().bar; set(value) { this.asDynamic().bar = value }

inline operator fun Event.get(prop: String): Number? = this.asDynamic().get(prop)

inline operator fun Event.set(prop: String, value: Number) { this.asDynamic().set(prop, value) }

inline var Event.someField: String get() = this.asDynamic().someField; set(value) { this.asDynamic().someField = value }

inline var Event.optionalField: Any? get() = this.asDynamic().optionalField; set(value) { this.asDynamic().optionalField = value }

inline operator fun Event.invoke(resourceId: String, hash: Any? = null, callback: Function<*>? = null) { this.asDynamic().invoke(resourceId, hash, callback) }