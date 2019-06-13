@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")

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

external interface Map<K, V>
external interface List<T>
typealias Values<V> = List<V>
typealias MultiMap<K, V> = Map<K, Values<V>>
typealias MyHeaders = MultiMap<String, String>
external interface `T$0`<T> {
    var ip: T
}
typealias Ping<T> = (packet: `T$0`<T>) -> Boolean
external var fooMap: MultiMap<String, Number> = definedExternally
external fun mapKey(a: MultiMap<Number, String>): Unit
external var fooStringOrMap: dynamic /* String | MultiMap<String, Number> */ = definedExternally
external fun stringOrMapKey(a: String): Unit
external fun stringOrMapKey(a: MultiMap<Number, String>): Unit
external var listOfStringOrNumber: dynamic /* String | List<dynamic /* String | Number */> */ = definedExternally
external fun listOfNumberOrString(a: List<dynamic /* Number | String */>): Unit
external var headers: MyHeaders = definedExternally
external fun getHeaders(): MyHeaders
external fun addHeaders(headers: MyHeaders): Unit
external var someRef: dynamic /* String | (instance: Number) -> Any */ = definedExternally
external fun addRef(ref: String): Unit
external fun addRef(ref: (instance: Number) -> Any): Unit