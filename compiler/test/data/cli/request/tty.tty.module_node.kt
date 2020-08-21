@file:JsModule("tty")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package tty

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

external fun isatty(fd: Number): Boolean

external open class ReadStream(fd: Number, options: net.SocketConstructorOpts? = definedExternally /* null */) : net.Socket {
    open var isRaw: Boolean
    open fun setRawMode(mode: Boolean)
    open var isTTY: Boolean
}

external open class WriteStream(fd: Number) : net.Socket {
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): WriteStream /* this */
    open fun addListener(event: String /* "resize" */, listener: () -> Unit): WriteStream /* this */
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun emit(event: String /* "resize" */): Boolean
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): WriteStream /* this */
    open fun on(event: String /* "resize" */, listener: () -> Unit): WriteStream /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): WriteStream /* this */
    open fun once(event: String /* "resize" */, listener: () -> Unit): WriteStream /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): WriteStream /* this */
    open fun prependListener(event: String /* "resize" */, listener: () -> Unit): WriteStream /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): WriteStream /* this */
    open fun prependOnceListener(event: String /* "resize" */, listener: () -> Unit): WriteStream /* this */
    open fun clearLine(dir: String /* -1 */, callback: (() -> Unit)? = definedExternally /* null */): Boolean
    open fun clearLine(dir: String /* 0 */, callback: (() -> Unit)? = definedExternally /* null */): Boolean
    open fun clearLine(dir: String /* 1 */, callback: (() -> Unit)? = definedExternally /* null */): Boolean
    open fun clearScreenDown(callback: (() -> Unit)? = definedExternally /* null */): Boolean
    open fun cursorTo(x: Number, y: Number? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): Boolean
    open fun cursorTo(x: Number, callback: () -> Unit): Boolean
    open fun moveCursor(dx: Number, dy: Number, callback: (() -> Unit)? = definedExternally /* null */): Boolean
    open fun getColorDepth(env: Any? = definedExternally /* null */): Number
    open fun hasColors(depth: Number? = definedExternally /* null */): Boolean
    open fun hasColors(env: Any? = definedExternally /* null */): Boolean
    open fun hasColors(depth: Number, env: Any? = definedExternally /* null */): Boolean
    open fun getWindowSize(): dynamic /* JsTuple<Number, Number> */
    open var columns: Number
    open var rows: Number
    open var isTTY: Boolean
    open fun hasColors(): Boolean
}