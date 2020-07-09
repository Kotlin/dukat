@file:JsModule("querystring")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package querystring

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

external interface StringifyOptions {
    var encodeURIComponent: ((str: String) -> String)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ParseOptions {
    var maxKeys: Number?
        get() = definedExternally
        set(value) = definedExternally
    var decodeURIComponent: ((str: String) -> String)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ParsedUrlQuery {
    @nativeGetter
    operator fun get(key: String): dynamic /* String | Array<String> */
    @nativeSetter
    operator fun set(key: String, value: String)
    @nativeSetter
    operator fun set(key: String, value: Array<String>)
}

external interface ParsedUrlQueryInput {
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any?)
}

external fun stringify(obj: ParsedUrlQueryInput? = definedExternally /* null */, sep: String? = definedExternally /* null */, eq: String? = definedExternally /* null */, options: StringifyOptions? = definedExternally /* null */): String

external fun parse(str: String, sep: String? = definedExternally /* null */, eq: String? = definedExternally /* null */, options: ParseOptions? = definedExternally /* null */): ParsedUrlQuery

external var encode: Any

external var decode: Any

external fun escape(str: String): String

external fun unescape(str: String): String