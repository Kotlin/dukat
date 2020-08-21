@file:JsQualifier("fs.realpath")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package fs.realpath

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

external interface `T$6` {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
        get() = definedExternally
        set(value) = definedExternally
}

external fun __promisify__(path: String, options: dynamic /* `T$6` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): Promise<String>

external fun __promisify__(path: Buffer, options: dynamic /* `T$6` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): Promise<String>

external fun __promisify__(path: URL, options: dynamic /* `T$6` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): Promise<String>

external interface `T$7` {
    var encoding: String /* "buffer" */
}

external fun __promisify__(path: String, options: `T$7`): Promise<Buffer>

external fun __promisify__(path: String, options: String /* "buffer" */): Promise<Buffer>

external fun __promisify__(path: Buffer, options: `T$7`): Promise<Buffer>

external fun __promisify__(path: Buffer, options: String /* "buffer" */): Promise<Buffer>

external fun __promisify__(path: URL, options: `T$7`): Promise<Buffer>

external fun __promisify__(path: URL, options: String /* "buffer" */): Promise<Buffer>

external interface `T$8` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun __promisify__(path: String, options: `T$8`? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: String, options: String? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: String, options: Nothing? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: Buffer, options: `T$8`? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: Buffer, options: String? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: Buffer, options: Nothing? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: URL, options: `T$8`? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: URL, options: String? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: URL, options: Nothing? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun native(path: String, options: dynamic /* `T$6` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun native(path: Buffer, options: dynamic /* `T$6` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun native(path: URL, options: dynamic /* `T$6` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun native(path: String, options: `T$7`, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun native(path: String, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun native(path: Buffer, options: `T$7`, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun native(path: Buffer, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun native(path: URL, options: `T$7`, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun native(path: URL, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun native(path: String, options: `T$8`, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun native(path: String, options: String, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun native(path: String, options: Nothing?, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun native(path: Buffer, options: `T$8`, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun native(path: Buffer, options: String, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun native(path: Buffer, options: Nothing?, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun native(path: URL, options: `T$8`, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun native(path: URL, options: String, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun native(path: URL, options: Nothing?, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun native(path: String, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun native(path: Buffer, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun native(path: URL, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun __promisify__(path: String): Promise<String>

external fun __promisify__(path: Buffer): Promise<String>

external fun __promisify__(path: URL): Promise<String>