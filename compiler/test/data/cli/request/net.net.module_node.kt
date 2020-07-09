@file:JsModule("net")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package net

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

external interface AddressInfo {
    var address: String
    var family: String
    var port: Number
}

external interface SocketConstructorOpts {
    var fd: Number?
        get() = definedExternally
        set(value) = definedExternally
    var allowHalfOpen: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var readable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var writable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface OnReadOpts {
    var buffer: dynamic /* Uint8Array | () -> Uint8Array */
    fun callback(bytesWritten: Number, buf: Uint8Array): Boolean
}

external interface ConnectOpts {
    var onread: OnReadOpts?
        get() = definedExternally
        set(value) = definedExternally
}

external interface TcpSocketConnectOpts : ConnectOpts {
    var port: Number
    var host: String?
        get() = definedExternally
        set(value) = definedExternally
    var localAddress: String?
        get() = definedExternally
        set(value) = definedExternally
    var localPort: Number?
        get() = definedExternally
        set(value) = definedExternally
    var hints: Number?
        get() = definedExternally
        set(value) = definedExternally
    var family: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lookup: LookupFunction?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IpcSocketConnectOpts : ConnectOpts {
    var path: String
}

external open class Socket(options: SocketConstructorOpts? = definedExternally /* null */) : stream.Duplex {
    open fun write(buffer: Uint8Array, cb: ((err: Error? /* = null */) -> Unit)? = definedExternally /* null */): Boolean
    open fun write(buffer: String, cb: ((err: Error? /* = null */) -> Unit)? = definedExternally /* null */): Boolean
    open fun write(str: Uint8Array, encoding: String? = definedExternally /* null */, cb: ((err: Error? /* = null */) -> Unit)? = definedExternally /* null */): Boolean
    open fun write(str: String, encoding: String? = definedExternally /* null */, cb: ((err: Error? /* = null */) -> Unit)? = definedExternally /* null */): Boolean
    open fun connect(options: TcpSocketConnectOpts, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket /* this */
    open fun connect(options: IpcSocketConnectOpts, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket /* this */
    open fun connect(port: Number, host: String, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket /* this */
    open fun connect(port: Number, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket /* this */
    open fun connect(path: String, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket /* this */
    open fun setEncoding(encoding: String? = definedExternally /* null */): Socket /* this */
    open fun pause(): Socket /* this */
    open fun resume(): Socket /* this */
    open fun setTimeout(timeout: Number, callback: (() -> Unit)? = definedExternally /* null */): Socket /* this */
    open fun setNoDelay(noDelay: Boolean? = definedExternally /* null */): Socket /* this */
    open fun setKeepAlive(enable: Boolean? = definedExternally /* null */, initialDelay: Number? = definedExternally /* null */): Socket /* this */
    open fun address(): dynamic /* AddressInfo | String */
    open fun unref()
    open fun ref()
    open var bufferSize: Number
    open var bytesRead: Number
    open var bytesWritten: Number
    open var connecting: Boolean
    open var destroyed: Boolean
    open var localAddress: String
    open var localPort: Number
    open var remoteAddress: String
    open var remoteFamily: String
    open var remotePort: Number
    open fun end(cb: (() -> Unit)? = definedExternally /* null */)
    open fun end(buffer: Uint8Array, cb: (() -> Unit)? = definedExternally /* null */)
    open fun end(buffer: String, cb: (() -> Unit)? = definedExternally /* null */)
    open fun end(str: Uint8Array, encoding: String? = definedExternally /* null */, cb: (() -> Unit)? = definedExternally /* null */)
    open fun end(str: String, encoding: String? = definedExternally /* null */, cb: (() -> Unit)? = definedExternally /* null */)
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Socket /* this */
    open fun addListener(event: String /* "close" */, listener: (had_error: Boolean) -> Unit): Socket /* this */
    open fun addListener(event: String /* "connect" */, listener: () -> Unit): Socket /* this */
    open fun addListener(event: String /* "data" */, listener: (data: Buffer) -> Unit): Socket /* this */
    open fun addListener(event: String /* "drain" */, listener: () -> Unit): Socket /* this */
    open fun addListener(event: String /* "end" */, listener: () -> Unit): Socket /* this */
    open fun addListener(event: String /* "error" */, listener: (err: Error) -> Unit): Socket /* this */
    open fun addListener(event: String /* "lookup" */, listener: (err: Error, address: String, family: dynamic /* String | Number */, host: String) -> Unit): Socket /* this */
    open fun addListener(event: String /* "timeout" */, listener: () -> Unit): Socket /* this */
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun emit(event: String /* "close" */, had_error: Boolean): Boolean
    open fun emit(event: String /* "connect" */): Boolean
    open fun emit(event: String /* "data" */, data: Buffer): Boolean
    open fun emit(event: String /* "drain" */): Boolean
    open fun emit(event: String /* "end" */): Boolean
    open fun emit(event: String /* "error" */, err: Error): Boolean
    open fun emit(event: String /* "lookup" */, err: Error, address: String, family: String, host: String): Boolean
    open fun emit(event: String /* "lookup" */, err: Error, address: String, family: Number, host: String): Boolean
    open fun emit(event: String /* "timeout" */): Boolean
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): Socket /* this */
    open fun on(event: String /* "close" */, listener: (had_error: Boolean) -> Unit): Socket /* this */
    open fun on(event: String /* "connect" */, listener: () -> Unit): Socket /* this */
    open fun on(event: String /* "data" */, listener: (data: Buffer) -> Unit): Socket /* this */
    open fun on(event: String /* "drain" */, listener: () -> Unit): Socket /* this */
    open fun on(event: String /* "end" */, listener: () -> Unit): Socket /* this */
    open fun on(event: String /* "error" */, listener: (err: Error) -> Unit): Socket /* this */
    open fun on(event: String /* "lookup" */, listener: (err: Error, address: String, family: dynamic /* String | Number */, host: String) -> Unit): Socket /* this */
    open fun on(event: String /* "timeout" */, listener: () -> Unit): Socket /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): Socket /* this */
    open fun once(event: String /* "close" */, listener: (had_error: Boolean) -> Unit): Socket /* this */
    open fun once(event: String /* "connect" */, listener: () -> Unit): Socket /* this */
    open fun once(event: String /* "data" */, listener: (data: Buffer) -> Unit): Socket /* this */
    open fun once(event: String /* "drain" */, listener: () -> Unit): Socket /* this */
    open fun once(event: String /* "end" */, listener: () -> Unit): Socket /* this */
    open fun once(event: String /* "error" */, listener: (err: Error) -> Unit): Socket /* this */
    open fun once(event: String /* "lookup" */, listener: (err: Error, address: String, family: dynamic /* String | Number */, host: String) -> Unit): Socket /* this */
    open fun once(event: String /* "timeout" */, listener: () -> Unit): Socket /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Socket /* this */
    open fun prependListener(event: String /* "close" */, listener: (had_error: Boolean) -> Unit): Socket /* this */
    open fun prependListener(event: String /* "connect" */, listener: () -> Unit): Socket /* this */
    open fun prependListener(event: String /* "data" */, listener: (data: Buffer) -> Unit): Socket /* this */
    open fun prependListener(event: String /* "drain" */, listener: () -> Unit): Socket /* this */
    open fun prependListener(event: String /* "end" */, listener: () -> Unit): Socket /* this */
    open fun prependListener(event: String /* "error" */, listener: (err: Error) -> Unit): Socket /* this */
    open fun prependListener(event: String /* "lookup" */, listener: (err: Error, address: String, family: dynamic /* String | Number */, host: String) -> Unit): Socket /* this */
    open fun prependListener(event: String /* "timeout" */, listener: () -> Unit): Socket /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "close" */, listener: (had_error: Boolean) -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "connect" */, listener: () -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "data" */, listener: (data: Buffer) -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "drain" */, listener: () -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "end" */, listener: () -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "error" */, listener: (err: Error) -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "lookup" */, listener: (err: Error, address: String, family: dynamic /* String | Number */, host: String) -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "timeout" */, listener: () -> Unit): Socket /* this */
}

external interface ListenOptions {
    var port: Number?
        get() = definedExternally
        set(value) = definedExternally
    var host: String?
        get() = definedExternally
        set(value) = definedExternally
    var backlog: Number?
        get() = definedExternally
        set(value) = definedExternally
    var path: String?
        get() = definedExternally
        set(value) = definedExternally
    var exclusive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var readableAll: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var writableAll: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var ipv6Only: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$0` {
    var allowHalfOpen: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var pauseOnConnect: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external open class Server(connectionListener: ((socket: Socket) -> Unit)? = definedExternally /* null */) : events.EventEmitter {
    constructor(options: `T$0`?, connectionListener: ((socket: Socket) -> Unit)?)
    open fun listen(port: Number? = definedExternally /* null */, hostname: String? = definedExternally /* null */, backlog: Number? = definedExternally /* null */, listeningListener: (() -> Unit)? = definedExternally /* null */): Server /* this */
    open fun listen(port: Number? = definedExternally /* null */, hostname: String? = definedExternally /* null */, listeningListener: (() -> Unit)? = definedExternally /* null */): Server /* this */
    open fun listen(port: Number? = definedExternally /* null */, backlog: Number? = definedExternally /* null */, listeningListener: (() -> Unit)? = definedExternally /* null */): Server /* this */
    open fun listen(port: Number? = definedExternally /* null */, listeningListener: (() -> Unit)? = definedExternally /* null */): Server /* this */
    open fun listen(path: String, backlog: Number? = definedExternally /* null */, listeningListener: (() -> Unit)? = definedExternally /* null */): Server /* this */
    open fun listen(path: String, listeningListener: (() -> Unit)? = definedExternally /* null */): Server /* this */
    open fun listen(options: ListenOptions, listeningListener: (() -> Unit)? = definedExternally /* null */): Server /* this */
    open fun listen(handle: Any, backlog: Number? = definedExternally /* null */, listeningListener: (() -> Unit)? = definedExternally /* null */): Server /* this */
    open fun listen(handle: Any, listeningListener: (() -> Unit)? = definedExternally /* null */): Server /* this */
    open fun close(callback: ((err: Error? /* = null */) -> Unit)? = definedExternally /* null */): Server /* this */
    open fun address(): dynamic /* AddressInfo | String | Nothing? */
    open fun getConnections(cb: (error: Error?, count: Number) -> Unit)
    open fun ref(): Server /* this */
    open fun unref(): Server /* this */
    open var maxConnections: Number
    open var connections: Number
    open var listening: Boolean
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Server /* this */
    open fun addListener(event: String /* "close" */, listener: () -> Unit): Server /* this */
    open fun addListener(event: String /* "connection" */, listener: (socket: Socket) -> Unit): Server /* this */
    open fun addListener(event: String /* "error" */, listener: (err: Error) -> Unit): Server /* this */
    open fun addListener(event: String /* "listening" */, listener: () -> Unit): Server /* this */
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun emit(event: String /* "close" */): Boolean
    open fun emit(event: String /* "connection" */, socket: Socket): Boolean
    open fun emit(event: String /* "error" */, err: Error): Boolean
    open fun emit(event: String /* "listening" */): Boolean
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): Server /* this */
    open fun on(event: String /* "close" */, listener: () -> Unit): Server /* this */
    open fun on(event: String /* "connection" */, listener: (socket: Socket) -> Unit): Server /* this */
    open fun on(event: String /* "error" */, listener: (err: Error) -> Unit): Server /* this */
    open fun on(event: String /* "listening" */, listener: () -> Unit): Server /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): Server /* this */
    open fun once(event: String /* "close" */, listener: () -> Unit): Server /* this */
    open fun once(event: String /* "connection" */, listener: (socket: Socket) -> Unit): Server /* this */
    open fun once(event: String /* "error" */, listener: (err: Error) -> Unit): Server /* this */
    open fun once(event: String /* "listening" */, listener: () -> Unit): Server /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Server /* this */
    open fun prependListener(event: String /* "close" */, listener: () -> Unit): Server /* this */
    open fun prependListener(event: String /* "connection" */, listener: (socket: Socket) -> Unit): Server /* this */
    open fun prependListener(event: String /* "error" */, listener: (err: Error) -> Unit): Server /* this */
    open fun prependListener(event: String /* "listening" */, listener: () -> Unit): Server /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Server /* this */
    open fun prependOnceListener(event: String /* "close" */, listener: () -> Unit): Server /* this */
    open fun prependOnceListener(event: String /* "connection" */, listener: (socket: Socket) -> Unit): Server /* this */
    open fun prependOnceListener(event: String /* "error" */, listener: (err: Error) -> Unit): Server /* this */
    open fun prependOnceListener(event: String /* "listening" */, listener: () -> Unit): Server /* this */
}

external interface TcpNetConnectOpts : TcpSocketConnectOpts, SocketConstructorOpts {
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IpcNetConnectOpts : IpcSocketConnectOpts, SocketConstructorOpts {
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external fun createServer(connectionListener: ((socket: Socket) -> Unit)? = definedExternally /* null */): Server

external fun createServer(options: `T$0`? = definedExternally /* null */, connectionListener: ((socket: Socket) -> Unit)? = definedExternally /* null */): Server

external fun connect(options: TcpNetConnectOpts, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket

external fun connect(options: IpcNetConnectOpts, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket

external fun connect(port: Number, host: String? = definedExternally /* null */, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket

external fun connect(path: String, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket

external fun createConnection(options: TcpNetConnectOpts, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket

external fun createConnection(options: IpcNetConnectOpts, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket

external fun createConnection(port: Number, host: String? = definedExternally /* null */, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket

external fun createConnection(path: String, connectionListener: (() -> Unit)? = definedExternally /* null */): Socket

external fun isIP(input: String): Number

external fun isIPv4(input: String): Boolean

external fun isIPv6(input: String): Boolean