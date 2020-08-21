@file:JsModule("dgram")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package dgram

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

external interface RemoteInfo {
    var address: String
    var family: dynamic /* 'IPv4' | 'IPv6' */
    var port: Number
    var size: Number
}

external interface BindOptions {
    var port: Number
    var address: String?
        get() = definedExternally
        set(value) = definedExternally
    var exclusive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SocketOptions {
    var type: dynamic /* "udp4" | "udp6" */
    var reuseAddr: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var ipv6Only: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var recvBufferSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sendBufferSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lookup: ((hostname: String, options: dns.LookupOneOptions, callback: (err: NodeJS.ErrnoException?, address: String, family: Number) -> Unit) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

external fun createSocket(type: String /* "udp4" */, callback: ((msg: Buffer, rinfo: RemoteInfo) -> Unit)? = definedExternally /* null */): Socket

external fun createSocket(options: SocketOptions, callback: ((msg: Buffer, rinfo: RemoteInfo) -> Unit)? = definedExternally /* null */): Socket

external open class Socket : events.EventEmitter {
    open fun send(msg: String, port: Number, address: String? = definedExternally /* null */, callback: ((error: Error?, bytes: Number) -> Unit)? = definedExternally /* null */)
    open fun send(msg: Uint8Array, port: Number, address: String? = definedExternally /* null */, callback: ((error: Error?, bytes: Number) -> Unit)? = definedExternally /* null */)
    open fun send(msg: Array<Any>, port: Number, address: String? = definedExternally /* null */, callback: ((error: Error?, bytes: Number) -> Unit)? = definedExternally /* null */)
    open fun send(msg: String, offset: Number, length: Number, port: Number, address: String? = definedExternally /* null */, callback: ((error: Error?, bytes: Number) -> Unit)? = definedExternally /* null */)
    open fun send(msg: Uint8Array, offset: Number, length: Number, port: Number, address: String? = definedExternally /* null */, callback: ((error: Error?, bytes: Number) -> Unit)? = definedExternally /* null */)
    open fun bind(port: Number? = definedExternally /* null */, address: String? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */)
    open fun bind(port: Number? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */)
    open fun bind(callback: (() -> Unit)? = definedExternally /* null */)
    open fun bind(options: BindOptions, callback: (() -> Unit)? = definedExternally /* null */)
    open fun close(callback: (() -> Unit)? = definedExternally /* null */)
    open fun address(): dynamic /* AddressInfo | String */
    open fun setBroadcast(flag: Boolean)
    open fun setTTL(ttl: Number)
    open fun setMulticastTTL(ttl: Number)
    open fun setMulticastInterface(multicastInterface: String)
    open fun setMulticastLoopback(flag: Boolean)
    open fun addMembership(multicastAddress: String, multicastInterface: String? = definedExternally /* null */)
    open fun dropMembership(multicastAddress: String, multicastInterface: String? = definedExternally /* null */)
    open fun ref(): Socket /* this */
    open fun unref(): Socket /* this */
    open fun setRecvBufferSize(size: Number)
    open fun setSendBufferSize(size: Number)
    open fun getRecvBufferSize(): Number
    open fun getSendBufferSize(): Number
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Socket /* this */
    open fun addListener(event: String /* "close" */, listener: () -> Unit): Socket /* this */
    open fun addListener(event: String /* "error" */, listener: (err: Error) -> Unit): Socket /* this */
    open fun addListener(event: String /* "listening" */, listener: () -> Unit): Socket /* this */
    open fun addListener(event: String /* "message" */, listener: (msg: Buffer, rinfo: RemoteInfo) -> Unit): Socket /* this */
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun emit(event: String /* "close" */): Boolean
    open fun emit(event: String /* "error" */, err: Error): Boolean
    open fun emit(event: String /* "listening" */): Boolean
    open fun emit(event: String /* "message" */, msg: Buffer, rinfo: RemoteInfo): Boolean
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): Socket /* this */
    open fun on(event: String /* "close" */, listener: () -> Unit): Socket /* this */
    open fun on(event: String /* "error" */, listener: (err: Error) -> Unit): Socket /* this */
    open fun on(event: String /* "listening" */, listener: () -> Unit): Socket /* this */
    open fun on(event: String /* "message" */, listener: (msg: Buffer, rinfo: RemoteInfo) -> Unit): Socket /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): Socket /* this */
    open fun once(event: String /* "close" */, listener: () -> Unit): Socket /* this */
    open fun once(event: String /* "error" */, listener: (err: Error) -> Unit): Socket /* this */
    open fun once(event: String /* "listening" */, listener: () -> Unit): Socket /* this */
    open fun once(event: String /* "message" */, listener: (msg: Buffer, rinfo: RemoteInfo) -> Unit): Socket /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Socket /* this */
    open fun prependListener(event: String /* "close" */, listener: () -> Unit): Socket /* this */
    open fun prependListener(event: String /* "error" */, listener: (err: Error) -> Unit): Socket /* this */
    open fun prependListener(event: String /* "listening" */, listener: () -> Unit): Socket /* this */
    open fun prependListener(event: String /* "message" */, listener: (msg: Buffer, rinfo: RemoteInfo) -> Unit): Socket /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "close" */, listener: () -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "error" */, listener: (err: Error) -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "listening" */, listener: () -> Unit): Socket /* this */
    open fun prependOnceListener(event: String /* "message" */, listener: (msg: Buffer, rinfo: RemoteInfo) -> Unit): Socket /* this */
}