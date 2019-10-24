@file:JsQualifier("child_process.execFile")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package child_process.execFile

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

external interface `T$10` {
    var stdout: String
    var stderr: String
}

external fun __promisify__(file: String): child_process.PromiseWithChild

external fun __promisify__(file: String, args: Array<String>?): child_process.PromiseWithChild

external interface `T$11` {
    var stdout: Buffer
    var stderr: Buffer
}

external fun __promisify__(file: String, options: child_process.ExecFileOptionsWithBufferEncoding): child_process.PromiseWithChild

external fun __promisify__(file: String, args: Array<String>?, options: child_process.ExecFileOptionsWithBufferEncoding): child_process.PromiseWithChild

external fun __promisify__(file: String, options: child_process.ExecFileOptionsWithStringEncoding): child_process.PromiseWithChild

external fun __promisify__(file: String, args: Array<String>?, options: child_process.ExecFileOptionsWithStringEncoding): child_process.PromiseWithChild

external interface `T$12` {
    var stdout: dynamic /* String | Buffer */
    var stderr: dynamic /* String | Buffer */
}

external fun __promisify__(file: String, options: child_process.ExecFileOptionsWithOtherEncoding): child_process.PromiseWithChild

external fun __promisify__(file: String, args: Array<String>?, options: child_process.ExecFileOptionsWithOtherEncoding): child_process.PromiseWithChild

external fun __promisify__(file: String, options: child_process.ExecFileOptions): child_process.PromiseWithChild

external fun __promisify__(file: String, args: Array<String>?, options: child_process.ExecFileOptions): child_process.PromiseWithChild

external interface `T$13` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun __promisify__(file: String, options: `T$13` /* `T$13` & ExecFileOptions */): child_process.PromiseWithChild

external fun __promisify__(file: String, args: Array<String>?, options: `T$13` /* `T$13` & ExecFileOptions */): child_process.PromiseWithChild