@file:JsQualifier("fs.realpathSync")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package fs.realpathSync

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

external interface `T$9` {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
        get() = definedExternally
        set(value) = definedExternally
}

external fun native(path: String, options: dynamic /* `T$9` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): String

external fun native(path: Buffer, options: dynamic /* `T$9` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): String

external fun native(path: URL, options: dynamic /* `T$9` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): String

external interface `T$10` {
    var encoding: String /* "buffer" */
}

external fun native(path: String, options: `T$10`): Buffer

external fun native(path: String, options: String /* "buffer" */): Buffer

external fun native(path: Buffer, options: `T$10`): Buffer

external fun native(path: Buffer, options: String /* "buffer" */): Buffer

external fun native(path: URL, options: `T$10`): Buffer

external fun native(path: URL, options: String /* "buffer" */): Buffer

external interface `T$11` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun native(path: String, options: `T$11`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun native(path: String, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun native(path: String, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun native(path: Buffer, options: `T$11`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun native(path: Buffer, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun native(path: Buffer, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun native(path: URL, options: `T$11`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun native(path: URL, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun native(path: URL, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun native(path: String): String

external fun native(path: Buffer): String

external fun native(path: URL): String