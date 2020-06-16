// [test] typeParams.kt
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

external interface MapLike<K, V>

external interface List<T>

typealias Values<V> = List<V>

typealias MultiMap<K, V> = MapLike<K, Values<V>>

typealias MyHeaders = MultiMap<String, String>

external interface `T$1`<T> {
    var ip: T
}

typealias Ping<T> = (packet: `T$1`<T>) -> Boolean

external var fooMap: MultiMap<String, Number>

external fun mapKey(a: MultiMap<Number, String>)

external var fooStringOrMap: dynamic /* String | MultiMap<String, Number> */

external fun stringOrMapKey(a: String)

external fun stringOrMapKey(a: MultiMap<Number, String>)

external var listOfStringOrNumber: dynamic /* String | List<dynamic /* String | Number */> */

external fun listOfNumberOrString(a: List<dynamic /* Number | String */>)

external var headers: MyHeaders

external fun getHeaders(): MyHeaders

external fun addHeaders(headers: MyHeaders)

external var someRef: dynamic /* String | (instance: Number) -> Any */

external fun addRef(ref: String)

external fun addRef(ref: (instance: Number) -> Any)

external interface `T$0`<T> {
    @nativeInvoke
    operator fun <T : (args: Array<Any>) -> Any> invoke(func: T, resolver: (args: Array<Any>) -> Any = definedExternally): T
}

external interface SomeInterface {
    var cached: `T$0`<Any?>
}