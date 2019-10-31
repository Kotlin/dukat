@file:JsQualifier("fs.mkdtemp")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package fs.mkdtemp

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

external interface `T$12` {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
        get() = definedExternally
        set(value) = definedExternally
}

external fun __promisify__(prefix: String, options: `T$12`? = definedExternally /* null */): Promise<String>

external fun __promisify__(prefix: String, options: String /* "ascii" */ = definedExternally /* null */): Promise<String>

external fun __promisify__(prefix: String, options: Nothing? = definedExternally /* null */): Promise<String>

external interface `T$13` {
    var encoding: String /* "buffer" */
}

external fun __promisify__(prefix: String, options: `T$13`): Promise<Buffer>

external fun __promisify__(prefix: String, options: String /* "buffer" */): Promise<Buffer>

external interface `T$14` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun __promisify__(prefix: String, options: `T$14`? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(prefix: String, options: String? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(prefix: String, options: Nothing? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(prefix: String): Promise<String>