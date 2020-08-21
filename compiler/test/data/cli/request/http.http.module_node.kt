@file:JsModule("http")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package http

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

external interface IncomingHttpHeaders {
    var `accept`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `accept-language`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `accept-patch`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `accept-ranges`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `access-control-allow-credentials`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `access-control-allow-headers`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `access-control-allow-methods`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `access-control-allow-origin`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `access-control-expose-headers`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `access-control-max-age`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `age`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `allow`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `alt-svc`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `authorization`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `cache-control`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `connection`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `content-disposition`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `content-encoding`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `content-language`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `content-length`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `content-location`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `content-range`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `content-type`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `cookie`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `date`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `expect`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `expires`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `forwarded`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `from`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `host`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `if-match`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `if-modified-since`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `if-none-match`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `if-unmodified-since`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `last-modified`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `location`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `pragma`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `proxy-authenticate`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `proxy-authorization`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `public-key-pins`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `range`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `referer`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `retry-after`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `set-cookie`: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var `strict-transport-security`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `tk`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `trailer`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `transfer-encoding`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `upgrade`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `user-agent`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `vary`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `via`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `warning`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `www-authenticate`: String?
        get() = definedExternally
        set(value) = definedExternally
    @nativeGetter
    operator fun get(header: String): dynamic /* String | Array<String> | Nothing? */
    @nativeSetter
    operator fun set(header: String, value: String)
    @nativeSetter
    operator fun set(header: String, value: Array<String>)
    @nativeSetter
    operator fun set(header: String, value: Nothing?)
}

external interface OutgoingHttpHeaders {
    @nativeGetter
    operator fun get(header: String): dynamic /* Number | String | Array<String> | Nothing? */
    @nativeSetter
    operator fun set(header: String, value: Number)
    @nativeSetter
    operator fun set(header: String, value: String)
    @nativeSetter
    operator fun set(header: String, value: Array<String>)
    @nativeSetter
    operator fun set(header: String, value: Nothing?)
}

external interface ClientRequestArgs {
    var protocol: String?
        get() = definedExternally
        set(value) = definedExternally
    var host: String?
        get() = definedExternally
        set(value) = definedExternally
    var hostname: String?
        get() = definedExternally
        set(value) = definedExternally
    var family: Number?
        get() = definedExternally
        set(value) = definedExternally
    var port: dynamic /* Number | String */
        get() = definedExternally
        set(value) = definedExternally
    var defaultPort: dynamic /* Number | String */
        get() = definedExternally
        set(value) = definedExternally
    var localAddress: String?
        get() = definedExternally
        set(value) = definedExternally
    var socketPath: String?
        get() = definedExternally
        set(value) = definedExternally
    var method: String?
        get() = definedExternally
        set(value) = definedExternally
    var path: String?
        get() = definedExternally
        set(value) = definedExternally
    var headers: OutgoingHttpHeaders?
        get() = definedExternally
        set(value) = definedExternally
    var auth: String?
        get() = definedExternally
        set(value) = definedExternally
    var agent: dynamic /* Agent | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var _defaultAgent: Agent?
        get() = definedExternally
        set(value) = definedExternally
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
    var setHost: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var createConnection: ((options: ClientRequestArgs, oncreate: (err: Error, socket: Socket) -> Unit) -> Socket)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ServerOptions {
    var IncomingMessage: Any?
        get() = definedExternally
        set(value) = definedExternally
    var ServerResponse: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external open class Server(requestListener: RequestListener? = definedExternally /* null */) : NetServer {
    constructor(options: ServerOptions, requestListener: RequestListener?)
    open fun setTimeout(msecs: Number? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): Server /* this */
    open fun setTimeout(callback: () -> Unit): Server /* this */
    open var maxHeadersCount: Number?
    open var timeout: Number
    open var headersTimeout: Number
    open var keepAliveTimeout: Number
}

external open class OutgoingMessage : stream.Writable {
    open var upgrading: Boolean
    open var chunkedEncoding: Boolean
    open var shouldKeepAlive: Boolean
    open var useChunkedEncodingByDefault: Boolean
    open var sendDate: Boolean
    open var finished: Boolean
    open var headersSent: Boolean
    open var connection: Socket
    open fun setTimeout(msecs: Number, callback: (() -> Unit)? = definedExternally /* null */): OutgoingMessage /* this */
    open fun setHeader(name: String, value: Number)
    open fun setHeader(name: String, value: String)
    open fun setHeader(name: String, value: Array<String>)
    open fun getHeader(name: String): dynamic /* Number | String | Array<String> | Nothing? */
    open fun getHeaders(): OutgoingHttpHeaders
    open fun getHeaderNames(): Array<String>
    open fun hasHeader(name: String): Boolean
    open fun removeHeader(name: String)
    open fun addTrailers(headers: OutgoingHttpHeaders)
    open fun addTrailers(headers: Array<dynamic /* JsTuple<String, String> */>)
    open fun flushHeaders()
}

external open class ServerResponse(req: IncomingMessage) : OutgoingMessage {
    open var statusCode: Number
    open var statusMessage: String
    open var writableFinished: Boolean
    open fun assignSocket(socket: Socket)
    open fun detachSocket(socket: Socket)
    open fun writeContinue(callback: (() -> Unit)? = definedExternally /* null */)
    open fun writeHead(statusCode: Number, reasonPhrase: String? = definedExternally /* null */, headers: OutgoingHttpHeaders? = definedExternally /* null */): ServerResponse /* this */
    open fun writeHead(statusCode: Number, headers: OutgoingHttpHeaders? = definedExternally /* null */): ServerResponse /* this */
}

external interface InformationEvent {
    var statusCode: Number
    var statusMessage: String
    var httpVersion: String
    var httpVersionMajor: Number
    var httpVersionMinor: Number
    var headers: IncomingHttpHeaders
    var rawHeaders: Array<String>
}

external open class ClientRequest : OutgoingMessage {
    constructor(url: String, cb: ((res: IncomingMessage) -> Unit)?)
    constructor(url: URL, cb: ((res: IncomingMessage) -> Unit)?)
    constructor(url: ClientRequestArgs, cb: ((res: IncomingMessage) -> Unit)?)
    override var connection: Socket
    open var socket: Socket
    open var aborted: Number
    open var path: String
    open fun abort()
    open fun onSocket(socket: Socket)
    override fun setTimeout(timeout: Number, callback: (() -> Unit)?): ClientRequest /* this */
    open fun setNoDelay(noDelay: Boolean? = definedExternally /* null */)
    open fun setSocketKeepAlive(enable: Boolean? = definedExternally /* null */, initialDelay: Number? = definedExternally /* null */)
    open fun addListener(event: String /* 'abort' */, listener: () -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'connect' */, listener: (response: IncomingMessage, socket: Socket, head: Buffer) -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'continue' */, listener: () -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'information' */, listener: (info: InformationEvent) -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'response' */, listener: (response: IncomingMessage) -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'socket' */, listener: (socket: Socket) -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'timeout' */, listener: () -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'upgrade' */, listener: (response: IncomingMessage, socket: Socket, head: Buffer) -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'close' */, listener: () -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'drain' */, listener: () -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'error' */, listener: (err: Error) -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'finish' */, listener: () -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'pipe' */, listener: (src: stream.Readable) -> Unit): ClientRequest /* this */
    open fun addListener(event: String /* 'unpipe' */, listener: (src: stream.Readable) -> Unit): ClientRequest /* this */
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): ClientRequest /* this */
    open fun addListener(event: Any, listener: (args: Array<Any>) -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'abort' */, listener: () -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'connect' */, listener: (response: IncomingMessage, socket: Socket, head: Buffer) -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'continue' */, listener: () -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'information' */, listener: (info: InformationEvent) -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'response' */, listener: (response: IncomingMessage) -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'socket' */, listener: (socket: Socket) -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'timeout' */, listener: () -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'upgrade' */, listener: (response: IncomingMessage, socket: Socket, head: Buffer) -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'close' */, listener: () -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'drain' */, listener: () -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'error' */, listener: (err: Error) -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'finish' */, listener: () -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'pipe' */, listener: (src: stream.Readable) -> Unit): ClientRequest /* this */
    open fun on(event: String /* 'unpipe' */, listener: (src: stream.Readable) -> Unit): ClientRequest /* this */
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): ClientRequest /* this */
    open fun on(event: Any, listener: (args: Array<Any>) -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'abort' */, listener: () -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'connect' */, listener: (response: IncomingMessage, socket: Socket, head: Buffer) -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'continue' */, listener: () -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'information' */, listener: (info: InformationEvent) -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'response' */, listener: (response: IncomingMessage) -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'socket' */, listener: (socket: Socket) -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'timeout' */, listener: () -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'upgrade' */, listener: (response: IncomingMessage, socket: Socket, head: Buffer) -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'close' */, listener: () -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'drain' */, listener: () -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'error' */, listener: (err: Error) -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'finish' */, listener: () -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'pipe' */, listener: (src: stream.Readable) -> Unit): ClientRequest /* this */
    open fun once(event: String /* 'unpipe' */, listener: (src: stream.Readable) -> Unit): ClientRequest /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): ClientRequest /* this */
    open fun once(event: Any, listener: (args: Array<Any>) -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'abort' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'connect' */, listener: (response: IncomingMessage, socket: Socket, head: Buffer) -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'continue' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'information' */, listener: (info: InformationEvent) -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'response' */, listener: (response: IncomingMessage) -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'socket' */, listener: (socket: Socket) -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'timeout' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'upgrade' */, listener: (response: IncomingMessage, socket: Socket, head: Buffer) -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'close' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'drain' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'error' */, listener: (err: Error) -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'finish' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'pipe' */, listener: (src: stream.Readable) -> Unit): ClientRequest /* this */
    open fun prependListener(event: String /* 'unpipe' */, listener: (src: stream.Readable) -> Unit): ClientRequest /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): ClientRequest /* this */
    open fun prependListener(event: Any, listener: (args: Array<Any>) -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'abort' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'connect' */, listener: (response: IncomingMessage, socket: Socket, head: Buffer) -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'continue' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'information' */, listener: (info: InformationEvent) -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'response' */, listener: (response: IncomingMessage) -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'socket' */, listener: (socket: Socket) -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'timeout' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'upgrade' */, listener: (response: IncomingMessage, socket: Socket, head: Buffer) -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'close' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'drain' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'error' */, listener: (err: Error) -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'finish' */, listener: () -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'pipe' */, listener: (src: stream.Readable) -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String /* 'unpipe' */, listener: (src: stream.Readable) -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): ClientRequest /* this */
    open fun prependOnceListener(event: Any, listener: (args: Array<Any>) -> Unit): ClientRequest /* this */
}

external interface `T$0` {
    @nativeGetter
    operator fun get(key: String): String?
    @nativeSetter
    operator fun set(key: String, value: String?)
}

external open class IncomingMessage(socket: Socket) : stream.Readable {
    open var httpVersion: String
    open var httpVersionMajor: Number
    open var httpVersionMinor: Number
    open var complete: Boolean
    open var connection: Socket
    open var headers: IncomingHttpHeaders
    open var rawHeaders: Array<String>
    open var trailers: `T$0`
    open var rawTrailers: Array<String>
    open fun setTimeout(msecs: Number, callback: (() -> Unit)? = definedExternally /* null */): IncomingMessage /* this */
    open var method: String
    open var url: String
    open var statusCode: Number
    open var statusMessage: String
    open var socket: Socket
    open fun destroy(error: Error? = definedExternally /* null */)
}

external interface AgentOptions {
    var keepAlive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var keepAliveMsecs: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxSockets: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxFreeSockets: Number?
        get() = definedExternally
        set(value) = definedExternally
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$1` {
    @nativeGetter
    operator fun get(key: String): Array<Socket>?
    @nativeSetter
    operator fun set(key: String, value: Array<Socket>)
}

external interface `T$2` {
    @nativeGetter
    operator fun get(key: String): Array<IncomingMessage>?
    @nativeSetter
    operator fun set(key: String, value: Array<IncomingMessage>)
}

external open class Agent(opts: AgentOptions? = definedExternally /* null */) {
    open var maxFreeSockets: Number
    open var maxSockets: Number
    open var sockets: `T$1`
    open var requests: `T$2`
    open fun destroy()
}

external var METHODS: Array<String>

external object STATUS_CODES {
    @nativeGetter
    operator fun get(errorCode: Number): String?
    @nativeSetter
    operator fun set(errorCode: Number, value: String?)
    @nativeGetter
    operator fun get(errorCode: String): String?
    @nativeSetter
    operator fun set(errorCode: String, value: String?)
}

external fun createServer(requestListener: RequestListener? = definedExternally /* null */): Server

external fun createServer(options: ServerOptions, requestListener: RequestListener? = definedExternally /* null */): Server

external interface RequestOptions : ClientRequestArgs

external fun request(options: RequestOptions, callback: ((res: IncomingMessage) -> Unit)? = definedExternally /* null */): ClientRequest

external fun request(options: String, callback: ((res: IncomingMessage) -> Unit)? = definedExternally /* null */): ClientRequest

external fun request(options: URL, callback: ((res: IncomingMessage) -> Unit)? = definedExternally /* null */): ClientRequest

external fun request(url: String, options: RequestOptions, callback: ((res: IncomingMessage) -> Unit)? = definedExternally /* null */): ClientRequest

external fun request(url: URL, options: RequestOptions, callback: ((res: IncomingMessage) -> Unit)? = definedExternally /* null */): ClientRequest

external fun get(options: RequestOptions, callback: ((res: IncomingMessage) -> Unit)? = definedExternally /* null */): ClientRequest

external fun get(options: String, callback: ((res: IncomingMessage) -> Unit)? = definedExternally /* null */): ClientRequest

external fun get(options: URL, callback: ((res: IncomingMessage) -> Unit)? = definedExternally /* null */): ClientRequest

external fun get(url: String, options: RequestOptions, callback: ((res: IncomingMessage) -> Unit)? = definedExternally /* null */): ClientRequest

external fun get(url: URL, options: RequestOptions, callback: ((res: IncomingMessage) -> Unit)? = definedExternally /* null */): ClientRequest

external var globalAgent: Agent

external var maxHeaderSize: Number