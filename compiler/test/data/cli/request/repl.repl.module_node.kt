@file:JsModule("repl")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package repl

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

external interface ReplOptions {
    var prompt: String?
        get() = definedExternally
        set(value) = definedExternally
    var input: NodeJS.ReadableStream?
        get() = definedExternally
        set(value) = definedExternally
    var output: NodeJS.WritableStream?
        get() = definedExternally
        set(value) = definedExternally
    var terminal: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var eval: REPLEval?
        get() = definedExternally
        set(value) = definedExternally
    var useColors: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var useGlobal: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var ignoreUndefined: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var writer: REPLWriter?
        get() = definedExternally
        set(value) = definedExternally
    var completer: dynamic /* Completer | AsyncCompleter */
        get() = definedExternally
        set(value) = definedExternally
    var replMode: dynamic /* Any */
        get() = definedExternally
        set(value) = definedExternally
    var breakEvalOnSigint: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$0` {
    var options: InspectOptions
}

external var writer: REPLWriter /* REPLWriter & `T$0` */

external interface REPLCommand {
    var help: String?
        get() = definedExternally
        set(value) = definedExternally
    var action: REPLCommandAction
}

external interface `T$1` {
    @nativeGetter
    operator fun get(name: String): REPLCommand?
    @nativeSetter
    operator fun set(name: String, value: REPLCommand?)
}

external open class REPLServer : Interface {
    open var context: Context
    open var inputStream: NodeJS.ReadableStream
    open var outputStream: NodeJS.WritableStream
    open var commands: `T$1`
    open var editorMode: Boolean
    open var underscoreAssigned: Boolean
    open var last: Any
    open var underscoreErrAssigned: Boolean
    open var lastError: Any
    open var eval: REPLEval
    open var useColors: Boolean
    open var useGlobal: Boolean
    open var ignoreUndefined: Boolean
    open var writer: REPLWriter
    open var completer: dynamic /* Completer | AsyncCompleter */
    open var replMode: dynamic /* Any */
    open fun defineCommand(keyword: String, cmd: REPLCommandAction)
    open fun defineCommand(keyword: String, cmd: REPLCommand)
    open fun displayPrompt(preserveCursor: Boolean? = definedExternally /* null */)
    open fun clearBufferedCommand()
    open fun setupHistory(path: String, cb: (err: Error?, repl: REPLServer /* this */) -> Unit)
    override fun addListener(event: String, listener: (args: Array<Any>) -> Unit): REPLServer /* this */
    override fun addListener(event: String /* "close" */, listener: () -> Unit): REPLServer /* this */
    override fun addListener(event: String /* "line" */, listener: (input: String) -> Unit): REPLServer /* this */
    override fun addListener(event: String /* "pause" */, listener: () -> Unit): REPLServer /* this */
    override fun addListener(event: String /* "resume" */, listener: () -> Unit): REPLServer /* this */
    override fun addListener(event: String /* "SIGCONT" */, listener: () -> Unit): REPLServer /* this */
    override fun addListener(event: String /* "SIGINT" */, listener: () -> Unit): REPLServer /* this */
    override fun addListener(event: String /* "SIGTSTP" */, listener: () -> Unit): REPLServer /* this */
    override fun addListener(event: String /* "exit" */, listener: () -> Unit): REPLServer /* this */
    open fun addListener(event: String /* "reset" */, listener: (context: Context) -> Unit): REPLServer /* this */
    override fun emit(event: String, vararg args: Any): Boolean
    override fun emit(event: Any, vararg args: Any): Boolean
    override fun emit(event: String /* "close" */): Boolean
    override fun emit(event: String /* "line" */, input: String): Boolean
    override fun emit(event: String /* "pause" */): Boolean
    override fun emit(event: String /* "resume" */): Boolean
    override fun emit(event: String /* "SIGCONT" */): Boolean
    override fun emit(event: String /* "SIGINT" */): Boolean
    override fun emit(event: String /* "SIGTSTP" */): Boolean
    override fun emit(event: String /* "exit" */): Boolean
    override fun emit(event: String /* "reset" */, context: Context): Boolean
    override fun on(event: String, listener: (args: Array<Any>) -> Unit): REPLServer /* this */
    override fun on(event: String /* "close" */, listener: () -> Unit): REPLServer /* this */
    override fun on(event: String /* "line" */, listener: (input: String) -> Unit): REPLServer /* this */
    override fun on(event: String /* "pause" */, listener: () -> Unit): REPLServer /* this */
    override fun on(event: String /* "resume" */, listener: () -> Unit): REPLServer /* this */
    override fun on(event: String /* "SIGCONT" */, listener: () -> Unit): REPLServer /* this */
    override fun on(event: String /* "SIGINT" */, listener: () -> Unit): REPLServer /* this */
    override fun on(event: String /* "SIGTSTP" */, listener: () -> Unit): REPLServer /* this */
    override fun on(event: String /* "exit" */, listener: () -> Unit): REPLServer /* this */
    open fun on(event: String /* "reset" */, listener: (context: Context) -> Unit): REPLServer /* this */
    override fun once(event: String, listener: (args: Array<Any>) -> Unit): REPLServer /* this */
    override fun once(event: String /* "close" */, listener: () -> Unit): REPLServer /* this */
    override fun once(event: String /* "line" */, listener: (input: String) -> Unit): REPLServer /* this */
    override fun once(event: String /* "pause" */, listener: () -> Unit): REPLServer /* this */
    override fun once(event: String /* "resume" */, listener: () -> Unit): REPLServer /* this */
    override fun once(event: String /* "SIGCONT" */, listener: () -> Unit): REPLServer /* this */
    override fun once(event: String /* "SIGINT" */, listener: () -> Unit): REPLServer /* this */
    override fun once(event: String /* "SIGTSTP" */, listener: () -> Unit): REPLServer /* this */
    override fun once(event: String /* "exit" */, listener: () -> Unit): REPLServer /* this */
    open fun once(event: String /* "reset" */, listener: (context: Context) -> Unit): REPLServer /* this */
    override fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): REPLServer /* this */
    override fun prependListener(event: String /* "close" */, listener: () -> Unit): REPLServer /* this */
    override fun prependListener(event: String /* "line" */, listener: (input: String) -> Unit): REPLServer /* this */
    override fun prependListener(event: String /* "pause" */, listener: () -> Unit): REPLServer /* this */
    override fun prependListener(event: String /* "resume" */, listener: () -> Unit): REPLServer /* this */
    override fun prependListener(event: String /* "SIGCONT" */, listener: () -> Unit): REPLServer /* this */
    override fun prependListener(event: String /* "SIGINT" */, listener: () -> Unit): REPLServer /* this */
    override fun prependListener(event: String /* "SIGTSTP" */, listener: () -> Unit): REPLServer /* this */
    override fun prependListener(event: String /* "exit" */, listener: () -> Unit): REPLServer /* this */
    open fun prependListener(event: String /* "reset" */, listener: (context: Context) -> Unit): REPLServer /* this */
    override fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): REPLServer /* this */
    override fun prependOnceListener(event: String /* "close" */, listener: () -> Unit): REPLServer /* this */
    override fun prependOnceListener(event: String /* "line" */, listener: (input: String) -> Unit): REPLServer /* this */
    override fun prependOnceListener(event: String /* "pause" */, listener: () -> Unit): REPLServer /* this */
    override fun prependOnceListener(event: String /* "resume" */, listener: () -> Unit): REPLServer /* this */
    override fun prependOnceListener(event: String /* "SIGCONT" */, listener: () -> Unit): REPLServer /* this */
    override fun prependOnceListener(event: String /* "SIGINT" */, listener: () -> Unit): REPLServer /* this */
    override fun prependOnceListener(event: String /* "SIGTSTP" */, listener: () -> Unit): REPLServer /* this */
    override fun prependOnceListener(event: String /* "exit" */, listener: () -> Unit): REPLServer /* this */
    open fun prependOnceListener(event: String /* "reset" */, listener: (context: Context) -> Unit): REPLServer /* this */
}

external var REPL_MODE_SLOPPY: Any

external var REPL_MODE_STRICT: Any

external fun start(options: String? = definedExternally /* null */): REPLServer

external fun start(options: ReplOptions? = definedExternally /* null */): REPLServer

external open class Recoverable(err: Error) : SyntaxError {
    open var err: Error
}

external fun start(): REPLServer