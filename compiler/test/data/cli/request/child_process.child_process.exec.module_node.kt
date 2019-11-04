@file:JsQualifier("child_process.exec")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package child_process.exec

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

external interface `T$4` {
    var stdout: String
    var stderr: String
}

external fun __promisify__(command: String): child_process.PromiseWithChild

external interface `T$5` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$6` {
    var stdout: Buffer
    var stderr: Buffer
}

external fun __promisify__(command: String, options: `T$5` /* `T$5` & ExecOptions */): child_process.PromiseWithChild

external interface `T$7` {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
        get() = definedExternally
        set(value) = definedExternally
}

external fun __promisify__(command: String, options: `T$7` /* `T$7` & ExecOptions */): child_process.PromiseWithChild

external fun __promisify__(command: String, options: child_process.ExecOptions): child_process.PromiseWithChild

external interface `T$8` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$9` {
    var stdout: dynamic /* String | Buffer */
        get() = definedExternally
        set(value) = definedExternally
    var stderr: dynamic /* String | Buffer */
        get() = definedExternally
        set(value) = definedExternally
}

external fun __promisify__(command: String, options: `T$8` /* `T$8` & ExecOptions */ = definedExternally /* null */): child_process.PromiseWithChild