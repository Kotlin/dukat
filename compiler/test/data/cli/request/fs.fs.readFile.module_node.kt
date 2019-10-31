@file:JsQualifier("fs.readFile")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package fs.readFile

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

external interface `T$29` {
    var encoding: Nothing?
        get() = definedExternally
        set(value) = definedExternally
    var flag: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun __promisify__(path: String, options: `T$29`? = definedExternally /* null */): Promise<Buffer>

external fun __promisify__(path: String, options: Nothing? = definedExternally /* null */): Promise<Buffer>

external fun __promisify__(path: Buffer, options: `T$29`? = definedExternally /* null */): Promise<Buffer>

external fun __promisify__(path: Buffer, options: Nothing? = definedExternally /* null */): Promise<Buffer>

external fun __promisify__(path: URL, options: `T$29`? = definedExternally /* null */): Promise<Buffer>

external fun __promisify__(path: URL, options: Nothing? = definedExternally /* null */): Promise<Buffer>

external fun __promisify__(path: Number, options: `T$29`? = definedExternally /* null */): Promise<Buffer>

external fun __promisify__(path: Number, options: Nothing? = definedExternally /* null */): Promise<Buffer>

external interface `T$30` {
    var encoding: String
    var flag: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun __promisify__(path: String, options: `T$30`): Promise<String>

external fun __promisify__(path: String, options: String): Promise<String>

external fun __promisify__(path: Buffer, options: `T$30`): Promise<String>

external fun __promisify__(path: Buffer, options: String): Promise<String>

external fun __promisify__(path: URL, options: `T$30`): Promise<String>

external fun __promisify__(path: URL, options: String): Promise<String>

external fun __promisify__(path: Number, options: `T$30`): Promise<String>

external fun __promisify__(path: Number, options: String): Promise<String>

external interface `T$31` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var flag: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun __promisify__(path: String, options: `T$31`? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: String, options: String? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: String, options: Nothing? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: Buffer, options: `T$31`? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: Buffer, options: String? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: Buffer, options: Nothing? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: URL, options: `T$31`? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: URL, options: String? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: URL, options: Nothing? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: Number, options: `T$31`? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: Number, options: String? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: Number, options: Nothing? = definedExternally /* null */): Promise<dynamic /* String | Buffer */>

external fun __promisify__(path: String): Promise<Buffer>

external fun __promisify__(path: Buffer): Promise<Buffer>

external fun __promisify__(path: URL): Promise<Buffer>

external fun __promisify__(path: Number): Promise<Buffer>