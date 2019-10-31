@file:JsModule("vm")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package vm

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

external interface Context {
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
}

external interface BaseOptions {
    var filename: String?
        get() = definedExternally
        set(value) = definedExternally
    var lineOffset: Number?
        get() = definedExternally
        set(value) = definedExternally
    var columnOffset: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ScriptOptions : BaseOptions {
    var displayErrors: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
    var cachedData: Buffer?
        get() = definedExternally
        set(value) = definedExternally
    var produceCachedData: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RunningScriptOptions : BaseOptions {
    var displayErrors: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
    var breakOnSigint: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CompileFunctionOptions : BaseOptions {
    var cachedData: Buffer?
        get() = definedExternally
        set(value) = definedExternally
    var produceCachedData: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var parsingContext: Context?
        get() = definedExternally
        set(value) = definedExternally
    var contextExtensions: Array<Any>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$0` {
    var strings: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wasm: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CreateContextOptions {
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var origin: String?
        get() = definedExternally
        set(value) = definedExternally
    var codeGeneration: `T$0`?
        get() = definedExternally
        set(value) = definedExternally
}

external open class Script(code: String, options: ScriptOptions? = definedExternally /* null */) {
    open fun runInContext(contextifiedSandbox: Context, options: RunningScriptOptions? = definedExternally /* null */): Any
    open fun runInNewContext(sandbox: Context? = definedExternally /* null */, options: RunningScriptOptions? = definedExternally /* null */): Any
    open fun runInThisContext(options: RunningScriptOptions? = definedExternally /* null */): Any
    open fun createCachedData(): Buffer
}

external fun createContext(sandbox: Context? = definedExternally /* null */, options: CreateContextOptions? = definedExternally /* null */): Context

external fun isContext(sandbox: Context): Boolean

external fun runInContext(code: String, contextifiedSandbox: Context, options: RunningScriptOptions? = definedExternally /* null */): Any

external fun runInContext(code: String, contextifiedSandbox: Context, options: String? = definedExternally /* null */): Any

external fun runInNewContext(code: String, sandbox: Context? = definedExternally /* null */, options: RunningScriptOptions? = definedExternally /* null */): Any

external fun runInNewContext(code: String, sandbox: Context? = definedExternally /* null */, options: String? = definedExternally /* null */): Any

external fun runInThisContext(code: String, options: RunningScriptOptions? = definedExternally /* null */): Any

external fun runInThisContext(code: String, options: String? = definedExternally /* null */): Any

external fun compileFunction(code: String, params: Array<String>, options: CompileFunctionOptions): Function<*>

external fun runInContext(code: String, contextifiedSandbox: Context): Any

external fun runInNewContext(code: String): Any

external fun runInThisContext(code: String): Any