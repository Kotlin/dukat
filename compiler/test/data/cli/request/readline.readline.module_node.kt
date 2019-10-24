@file:JsModule("readline")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package readline

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

external interface Key {
    var sequence: String?
        get() = definedExternally
        set(value) = definedExternally
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var ctrl: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var meta: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var shift: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external open class Interface(options: ReadLineOptions) : events.EventEmitter {
    constructor(input: NodeJS.ReadableStream, output: NodeJS.WritableStream?, completer: Completer?, terminal: Boolean?)
    constructor(input: NodeJS.ReadableStream, output: NodeJS.WritableStream?, completer: AsyncCompleter?, terminal: Boolean?)
    open var terminal: Boolean
    open fun setPrompt(prompt: String)
    open fun prompt(preserveCursor: Boolean? = definedExternally /* null */)
    open fun question(query: String, callback: (answer: String) -> Unit)
    open fun pause(): Interface /* this */
    open fun resume(): Interface /* this */
    open fun close()
    open fun write(data: String, key: Key? = definedExternally /* null */)
    open fun write(data: Buffer, key: Key? = definedExternally /* null */)
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Interface /* this */
    open fun addListener(event: String /* "close" */, listener: () -> Unit): Interface /* this */
    open fun addListener(event: String /* "line" */, listener: (input: String) -> Unit): Interface /* this */
    open fun addListener(event: String /* "pause" */, listener: () -> Unit): Interface /* this */
    open fun addListener(event: String /* "resume" */, listener: () -> Unit): Interface /* this */
    open fun addListener(event: String /* "SIGCONT" */, listener: () -> Unit): Interface /* this */
    open fun addListener(event: String /* "SIGINT" */, listener: () -> Unit): Interface /* this */
    open fun addListener(event: String /* "SIGTSTP" */, listener: () -> Unit): Interface /* this */
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun emit(event: String /* "close" */): Boolean
    open fun emit(event: String /* "line" */, input: String): Boolean
    open fun emit(event: String /* "pause" */): Boolean
    open fun emit(event: String /* "resume" */): Boolean
    open fun emit(event: String /* "SIGCONT" */): Boolean
    open fun emit(event: String /* "SIGINT" */): Boolean
    open fun emit(event: String /* "SIGTSTP" */): Boolean
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): Interface /* this */
    open fun on(event: String /* "close" */, listener: () -> Unit): Interface /* this */
    open fun on(event: String /* "line" */, listener: (input: String) -> Unit): Interface /* this */
    open fun on(event: String /* "pause" */, listener: () -> Unit): Interface /* this */
    open fun on(event: String /* "resume" */, listener: () -> Unit): Interface /* this */
    open fun on(event: String /* "SIGCONT" */, listener: () -> Unit): Interface /* this */
    open fun on(event: String /* "SIGINT" */, listener: () -> Unit): Interface /* this */
    open fun on(event: String /* "SIGTSTP" */, listener: () -> Unit): Interface /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): Interface /* this */
    open fun once(event: String /* "close" */, listener: () -> Unit): Interface /* this */
    open fun once(event: String /* "line" */, listener: (input: String) -> Unit): Interface /* this */
    open fun once(event: String /* "pause" */, listener: () -> Unit): Interface /* this */
    open fun once(event: String /* "resume" */, listener: () -> Unit): Interface /* this */
    open fun once(event: String /* "SIGCONT" */, listener: () -> Unit): Interface /* this */
    open fun once(event: String /* "SIGINT" */, listener: () -> Unit): Interface /* this */
    open fun once(event: String /* "SIGTSTP" */, listener: () -> Unit): Interface /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Interface /* this */
    open fun prependListener(event: String /* "close" */, listener: () -> Unit): Interface /* this */
    open fun prependListener(event: String /* "line" */, listener: (input: String) -> Unit): Interface /* this */
    open fun prependListener(event: String /* "pause" */, listener: () -> Unit): Interface /* this */
    open fun prependListener(event: String /* "resume" */, listener: () -> Unit): Interface /* this */
    open fun prependListener(event: String /* "SIGCONT" */, listener: () -> Unit): Interface /* this */
    open fun prependListener(event: String /* "SIGINT" */, listener: () -> Unit): Interface /* this */
    open fun prependListener(event: String /* "SIGTSTP" */, listener: () -> Unit): Interface /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Interface /* this */
    open fun prependOnceListener(event: String /* "close" */, listener: () -> Unit): Interface /* this */
    open fun prependOnceListener(event: String /* "line" */, listener: (input: String) -> Unit): Interface /* this */
    open fun prependOnceListener(event: String /* "pause" */, listener: () -> Unit): Interface /* this */
    open fun prependOnceListener(event: String /* "resume" */, listener: () -> Unit): Interface /* this */
    open fun prependOnceListener(event: String /* "SIGCONT" */, listener: () -> Unit): Interface /* this */
    open fun prependOnceListener(event: String /* "SIGINT" */, listener: () -> Unit): Interface /* this */
    open fun prependOnceListener(event: String /* "SIGTSTP" */, listener: () -> Unit): Interface /* this */
}

external interface ReadLineOptions {
    var input: NodeJS.ReadableStream
    var output: NodeJS.WritableStream?
        get() = definedExternally
        set(value) = definedExternally
    var completer: dynamic /* Completer | AsyncCompleter */
        get() = definedExternally
        set(value) = definedExternally
    var terminal: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var historySize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var prompt: String?
        get() = definedExternally
        set(value) = definedExternally
    var crlfDelay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var removeHistoryDuplicates: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external fun createInterface(input: NodeJS.ReadableStream, output: NodeJS.WritableStream? = definedExternally /* null */, completer: Completer? = definedExternally /* null */, terminal: Boolean? = definedExternally /* null */): Interface

external fun createInterface(input: NodeJS.ReadableStream, output: NodeJS.WritableStream? = definedExternally /* null */, completer: AsyncCompleter? = definedExternally /* null */, terminal: Boolean? = definedExternally /* null */): Interface

external fun createInterface(options: ReadLineOptions): Interface

external fun emitKeypressEvents(stream: NodeJS.ReadableStream, readlineInterface: Interface? = definedExternally /* null */)

external fun clearLine(stream: NodeJS.WritableStream, dir: String /* -1 */, callback: (() -> Unit)? = definedExternally /* null */): Boolean

external fun clearScreenDown(stream: NodeJS.WritableStream, callback: (() -> Unit)? = definedExternally /* null */): Boolean

external fun cursorTo(stream: NodeJS.WritableStream, x: Number, y: Number? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): Boolean

external fun moveCursor(stream: NodeJS.WritableStream, dx: Number, dy: Number, callback: (() -> Unit)? = definedExternally /* null */): Boolean

external fun createInterface(input: NodeJS.ReadableStream): Interface