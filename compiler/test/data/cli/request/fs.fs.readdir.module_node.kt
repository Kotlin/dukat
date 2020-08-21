@file:JsQualifier("fs.readdir")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package fs.readdir

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

external interface `T$19` {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
    var withFileTypes: String /* false */
}

external fun __promisify__(path: String, options: dynamic /* `T$19` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): Promise<Array<String>>

external fun __promisify__(path: Buffer, options: dynamic /* `T$19` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): Promise<Array<String>>

external fun __promisify__(path: URL, options: dynamic /* `T$19` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): Promise<Array<String>>

external interface `T$20` {
    var encoding: String /* "buffer" */
    var withFileTypes: String /* false */
}

external fun __promisify__(path: String, options: String /* "buffer" */): Promise<Array<Buffer>>

external fun __promisify__(path: String, options: `T$20`): Promise<Array<Buffer>>

external fun __promisify__(path: Buffer, options: String /* "buffer" */): Promise<Array<Buffer>>

external fun __promisify__(path: Buffer, options: `T$20`): Promise<Array<Buffer>>

external fun __promisify__(path: URL, options: String /* "buffer" */): Promise<Array<Buffer>>

external fun __promisify__(path: URL, options: `T$20`): Promise<Array<Buffer>>

external interface `T$21` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var withFileTypes: String /* false */
}

external fun __promisify__(path: String, options: `T$21`? = definedExternally /* null */): Promise<dynamic /* Array<String> | Array<Buffer> */>

external fun __promisify__(path: String, options: String? = definedExternally /* null */): Promise<dynamic /* Array<String> | Array<Buffer> */>

external fun __promisify__(path: String, options: Nothing? = definedExternally /* null */): Promise<dynamic /* Array<String> | Array<Buffer> */>

external fun __promisify__(path: Buffer, options: `T$21`? = definedExternally /* null */): Promise<dynamic /* Array<String> | Array<Buffer> */>

external fun __promisify__(path: Buffer, options: String? = definedExternally /* null */): Promise<dynamic /* Array<String> | Array<Buffer> */>

external fun __promisify__(path: Buffer, options: Nothing? = definedExternally /* null */): Promise<dynamic /* Array<String> | Array<Buffer> */>

external fun __promisify__(path: URL, options: `T$21`? = definedExternally /* null */): Promise<dynamic /* Array<String> | Array<Buffer> */>

external fun __promisify__(path: URL, options: String? = definedExternally /* null */): Promise<dynamic /* Array<String> | Array<Buffer> */>

external fun __promisify__(path: URL, options: Nothing? = definedExternally /* null */): Promise<dynamic /* Array<String> | Array<Buffer> */>

external interface `T$22` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var withFileTypes: String /* true */
}

external fun __promisify__(path: String, options: `T$22`): Promise<Array<fs.Dirent>>

external fun __promisify__(path: Buffer, options: `T$22`): Promise<Array<fs.Dirent>>

external fun __promisify__(path: URL, options: `T$22`): Promise<Array<fs.Dirent>>

external fun __promisify__(path: String): Promise<Array<String>>

external fun __promisify__(path: Buffer): Promise<Array<String>>

external fun __promisify__(path: URL): Promise<Array<String>>