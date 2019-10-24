@file:JsModule("http2")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package http2

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

external interface IncomingHttpStatusHeader {
    var `:status`: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IncomingHttpHeaders : Http1IncomingHttpHeaders {
    var `:path`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `:method`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `:authority`: String?
        get() = definedExternally
        set(value) = definedExternally
    var `:scheme`: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface StreamPriorityOptions {
    var exclusive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var parent: Number?
        get() = definedExternally
        set(value) = definedExternally
    var weight: Number?
        get() = definedExternally
        set(value) = definedExternally
    var silent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface StreamState {
    var localWindowSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var state: Number?
        get() = definedExternally
        set(value) = definedExternally
    var localClose: Number?
        get() = definedExternally
        set(value) = definedExternally
    var remoteClose: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sumDependencyWeight: Number?
        get() = definedExternally
        set(value) = definedExternally
    var weight: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ServerStreamResponseOptions {
    var endStream: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var waitForTrailers: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface StatOptions {
    var offset: Number
    var length: Number
}

external interface ServerStreamFileResponseOptions {
    var statCheck: ((stats: fs.Stats, headers: OutgoingHttpHeaders, statOptions: StatOptions) -> dynamic)?
        get() = definedExternally
        set(value) = definedExternally
    var waitForTrailers: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var offset: Number?
        get() = definedExternally
        set(value) = definedExternally
    var length: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ServerStreamFileResponseOptionsWithError : ServerStreamFileResponseOptions {
    var onError: ((err: NodeJS.ErrnoException) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

external open class Http2Stream : stream.Duplex {
    open var aborted: Boolean
    open var bufferSize: Number
    open var closed: Boolean
    open var destroyed: Boolean
    open var endAfterHeaders: Boolean
    open var id: Number
    open var pending: Boolean
    open var rstCode: Number
    open var sentHeaders: OutgoingHttpHeaders
    open var sentInfoHeaders: Array<OutgoingHttpHeaders>
    open var sentTrailers: OutgoingHttpHeaders
    open var session: Http2Session
    open var state: StreamState
    open fun close(code: Number? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */)
    open fun priority(options: StreamPriorityOptions)
    open fun setTimeout(msecs: Number, callback: (() -> Unit)? = definedExternally /* null */)
    open fun sendTrailers(headers: OutgoingHttpHeaders)
    open fun addListener(event: String /* "aborted" */, listener: () -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "close" */, listener: () -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "data" */, listener: (chunk: dynamic /* Buffer | String */) -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "drain" */, listener: () -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "end" */, listener: () -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "error" */, listener: (err: Error) -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "finish" */, listener: () -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "frameError" */, listener: (frameType: Number, errorCode: Number) -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "pipe" */, listener: (src: stream.Readable) -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "unpipe" */, listener: (src: stream.Readable) -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "streamClosed" */, listener: (code: Number) -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "timeout" */, listener: () -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "trailers" */, listener: (trailers: IncomingHttpHeaders, flags: Number) -> Unit): Http2Stream /* this */
    open fun addListener(event: String /* "wantTrailers" */, listener: () -> Unit): Http2Stream /* this */
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Http2Stream /* this */
    open fun addListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2Stream /* this */
    open fun emit(event: String /* "aborted" */): Boolean
    open fun emit(event: String /* "close" */): Boolean
    open fun emit(event: String /* "data" */, chunk: Buffer): Boolean
    open fun emit(event: String /* "data" */, chunk: String): Boolean
    open fun emit(event: String /* "drain" */): Boolean
    open fun emit(event: String /* "end" */): Boolean
    open fun emit(event: String /* "error" */, err: Error): Boolean
    open fun emit(event: String /* "finish" */): Boolean
    open fun emit(event: String /* "frameError" */, frameType: Number, errorCode: Number): Boolean
    open fun emit(event: String /* "pipe" */, src: stream.Readable): Boolean
    open fun emit(event: String /* "unpipe" */, src: stream.Readable): Boolean
    open fun emit(event: String /* "streamClosed" */, code: Number): Boolean
    open fun emit(event: String /* "timeout" */): Boolean
    open fun emit(event: String /* "trailers" */, trailers: IncomingHttpHeaders, flags: Number): Boolean
    open fun emit(event: String /* "wantTrailers" */): Boolean
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun on(event: String /* "aborted" */, listener: () -> Unit): Http2Stream /* this */
    open fun on(event: String /* "close" */, listener: () -> Unit): Http2Stream /* this */
    open fun on(event: String /* "data" */, listener: (chunk: dynamic /* Buffer | String */) -> Unit): Http2Stream /* this */
    open fun on(event: String /* "drain" */, listener: () -> Unit): Http2Stream /* this */
    open fun on(event: String /* "end" */, listener: () -> Unit): Http2Stream /* this */
    open fun on(event: String /* "error" */, listener: (err: Error) -> Unit): Http2Stream /* this */
    open fun on(event: String /* "finish" */, listener: () -> Unit): Http2Stream /* this */
    open fun on(event: String /* "frameError" */, listener: (frameType: Number, errorCode: Number) -> Unit): Http2Stream /* this */
    open fun on(event: String /* "pipe" */, listener: (src: stream.Readable) -> Unit): Http2Stream /* this */
    open fun on(event: String /* "unpipe" */, listener: (src: stream.Readable) -> Unit): Http2Stream /* this */
    open fun on(event: String /* "streamClosed" */, listener: (code: Number) -> Unit): Http2Stream /* this */
    open fun on(event: String /* "timeout" */, listener: () -> Unit): Http2Stream /* this */
    open fun on(event: String /* "trailers" */, listener: (trailers: IncomingHttpHeaders, flags: Number) -> Unit): Http2Stream /* this */
    open fun on(event: String /* "wantTrailers" */, listener: () -> Unit): Http2Stream /* this */
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): Http2Stream /* this */
    open fun on(event: Any, listener: (args: Array<Any>) -> Unit): Http2Stream /* this */
    open fun once(event: String /* "aborted" */, listener: () -> Unit): Http2Stream /* this */
    open fun once(event: String /* "close" */, listener: () -> Unit): Http2Stream /* this */
    open fun once(event: String /* "data" */, listener: (chunk: dynamic /* Buffer | String */) -> Unit): Http2Stream /* this */
    open fun once(event: String /* "drain" */, listener: () -> Unit): Http2Stream /* this */
    open fun once(event: String /* "end" */, listener: () -> Unit): Http2Stream /* this */
    open fun once(event: String /* "error" */, listener: (err: Error) -> Unit): Http2Stream /* this */
    open fun once(event: String /* "finish" */, listener: () -> Unit): Http2Stream /* this */
    open fun once(event: String /* "frameError" */, listener: (frameType: Number, errorCode: Number) -> Unit): Http2Stream /* this */
    open fun once(event: String /* "pipe" */, listener: (src: stream.Readable) -> Unit): Http2Stream /* this */
    open fun once(event: String /* "unpipe" */, listener: (src: stream.Readable) -> Unit): Http2Stream /* this */
    open fun once(event: String /* "streamClosed" */, listener: (code: Number) -> Unit): Http2Stream /* this */
    open fun once(event: String /* "timeout" */, listener: () -> Unit): Http2Stream /* this */
    open fun once(event: String /* "trailers" */, listener: (trailers: IncomingHttpHeaders, flags: Number) -> Unit): Http2Stream /* this */
    open fun once(event: String /* "wantTrailers" */, listener: () -> Unit): Http2Stream /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): Http2Stream /* this */
    open fun once(event: Any, listener: (args: Array<Any>) -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "aborted" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "close" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "data" */, listener: (chunk: dynamic /* Buffer | String */) -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "drain" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "end" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "error" */, listener: (err: Error) -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "finish" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "frameError" */, listener: (frameType: Number, errorCode: Number) -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "pipe" */, listener: (src: stream.Readable) -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "unpipe" */, listener: (src: stream.Readable) -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "streamClosed" */, listener: (code: Number) -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "timeout" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "trailers" */, listener: (trailers: IncomingHttpHeaders, flags: Number) -> Unit): Http2Stream /* this */
    open fun prependListener(event: String /* "wantTrailers" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Http2Stream /* this */
    open fun prependListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "aborted" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "close" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "data" */, listener: (chunk: dynamic /* Buffer | String */) -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "drain" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "end" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "error" */, listener: (err: Error) -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "finish" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "frameError" */, listener: (frameType: Number, errorCode: Number) -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "pipe" */, listener: (src: stream.Readable) -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "unpipe" */, listener: (src: stream.Readable) -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "streamClosed" */, listener: (code: Number) -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "timeout" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "trailers" */, listener: (trailers: IncomingHttpHeaders, flags: Number) -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String /* "wantTrailers" */, listener: () -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Http2Stream /* this */
    open fun prependOnceListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2Stream /* this */
}

external open class ClientHttp2Stream : Http2Stream {
    open fun addListener(event: String /* "continue" */, listener: () -> Any): ClientHttp2Stream /* this */
    open fun addListener(event: String /* "headers" */, listener: (headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Stream /* this */
    open fun addListener(event: String /* "push" */, listener: (headers: IncomingHttpHeaders, flags: Number) -> Unit): ClientHttp2Stream /* this */
    open fun addListener(event: String /* "response" */, listener: (headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Stream /* this */
    override fun addListener(event: String, listener: (args: Array<Any>) -> Unit): ClientHttp2Stream /* this */
    override fun addListener(event: Any, listener: (args: Array<Any>) -> Unit): ClientHttp2Stream /* this */
    override fun emit(event: String /* "continue" */): Boolean
    override fun emit(event: String /* "headers" */, headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number): Boolean
    override fun emit(event: String /* "push" */, headers: IncomingHttpHeaders, flags: Number): Boolean
    override fun emit(event: String /* "response" */, headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number): Boolean
    override fun emit(event: String, vararg args: Any): Boolean
    override fun emit(event: Any, vararg args: Any): Boolean
    open fun on(event: String /* "continue" */, listener: () -> Any): ClientHttp2Stream /* this */
    open fun on(event: String /* "headers" */, listener: (headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Stream /* this */
    open fun on(event: String /* "push" */, listener: (headers: IncomingHttpHeaders, flags: Number) -> Unit): ClientHttp2Stream /* this */
    open fun on(event: String /* "response" */, listener: (headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Stream /* this */
    override fun on(event: String, listener: (args: Array<Any>) -> Unit): ClientHttp2Stream /* this */
    override fun on(event: Any, listener: (args: Array<Any>) -> Unit): ClientHttp2Stream /* this */
    open fun once(event: String /* "continue" */, listener: () -> Any): ClientHttp2Stream /* this */
    open fun once(event: String /* "headers" */, listener: (headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Stream /* this */
    open fun once(event: String /* "push" */, listener: (headers: IncomingHttpHeaders, flags: Number) -> Unit): ClientHttp2Stream /* this */
    open fun once(event: String /* "response" */, listener: (headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Stream /* this */
    override fun once(event: String, listener: (args: Array<Any>) -> Unit): ClientHttp2Stream /* this */
    override fun once(event: Any, listener: (args: Array<Any>) -> Unit): ClientHttp2Stream /* this */
    open fun prependListener(event: String /* "continue" */, listener: () -> Any): ClientHttp2Stream /* this */
    open fun prependListener(event: String /* "headers" */, listener: (headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Stream /* this */
    open fun prependListener(event: String /* "push" */, listener: (headers: IncomingHttpHeaders, flags: Number) -> Unit): ClientHttp2Stream /* this */
    open fun prependListener(event: String /* "response" */, listener: (headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Stream /* this */
    override fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): ClientHttp2Stream /* this */
    override fun prependListener(event: Any, listener: (args: Array<Any>) -> Unit): ClientHttp2Stream /* this */
    open fun prependOnceListener(event: String /* "continue" */, listener: () -> Any): ClientHttp2Stream /* this */
    open fun prependOnceListener(event: String /* "headers" */, listener: (headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Stream /* this */
    open fun prependOnceListener(event: String /* "push" */, listener: (headers: IncomingHttpHeaders, flags: Number) -> Unit): ClientHttp2Stream /* this */
    open fun prependOnceListener(event: String /* "response" */, listener: (headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Stream /* this */
    override fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): ClientHttp2Stream /* this */
    override fun prependOnceListener(event: Any, listener: (args: Array<Any>) -> Unit): ClientHttp2Stream /* this */
}

external open class ServerHttp2Stream : Http2Stream {
    open fun additionalHeaders(headers: OutgoingHttpHeaders)
    open var headersSent: Boolean
    open var pushAllowed: Boolean
    open fun pushStream(headers: OutgoingHttpHeaders, callback: ((err: Error?, pushStream: ServerHttp2Stream, headers: OutgoingHttpHeaders) -> Unit)? = definedExternally /* null */)
    open fun pushStream(headers: OutgoingHttpHeaders, options: StreamPriorityOptions? = definedExternally /* null */, callback: ((err: Error?, pushStream: ServerHttp2Stream, headers: OutgoingHttpHeaders) -> Unit)? = definedExternally /* null */)
    open fun respond(headers: OutgoingHttpHeaders? = definedExternally /* null */, options: ServerStreamResponseOptions? = definedExternally /* null */)
    open fun respondWithFD(fd: Number, headers: OutgoingHttpHeaders? = definedExternally /* null */, options: ServerStreamFileResponseOptions? = definedExternally /* null */)
    open fun respondWithFile(path: String, headers: OutgoingHttpHeaders? = definedExternally /* null */, options: ServerStreamFileResponseOptionsWithError? = definedExternally /* null */)
}

external interface Settings {
    var headerTableSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var enablePush: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var initialWindowSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxFrameSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxConcurrentStreams: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxHeaderListSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var enableConnectProtocol: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ClientSessionRequestOptions {
    var endStream: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var exclusive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var parent: Number?
        get() = definedExternally
        set(value) = definedExternally
    var weight: Number?
        get() = definedExternally
        set(value) = definedExternally
    var waitForTrailers: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SessionState {
    var effectiveLocalWindowSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var effectiveRecvDataLength: Number?
        get() = definedExternally
        set(value) = definedExternally
    var nextStreamID: Number?
        get() = definedExternally
        set(value) = definedExternally
    var localWindowSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lastProcStreamID: Number?
        get() = definedExternally
        set(value) = definedExternally
    var remoteWindowSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var outboundQueueSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var deflateDynamicTableSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var inflateDynamicTableSize: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external open class Http2Session : events.EventEmitter {
    open var alpnProtocol: String
    open fun close(callback: (() -> Unit)? = definedExternally /* null */)
    open var closed: Boolean
    open var connecting: Boolean
    open fun destroy(error: Error? = definedExternally /* null */, code: Number? = definedExternally /* null */)
    open var destroyed: Boolean
    open var encrypted: Boolean
    open fun goaway(code: Number? = definedExternally /* null */, lastStreamID: Number? = definedExternally /* null */, opaqueData: Uint8Array = definedExternally /* null */)
    open fun goaway(code: Number? = definedExternally /* null */, lastStreamID: Number? = definedExternally /* null */, opaqueData: Uint8ClampedArray = definedExternally /* null */)
    open fun goaway(code: Number? = definedExternally /* null */, lastStreamID: Number? = definedExternally /* null */, opaqueData: Uint16Array = definedExternally /* null */)
    open fun goaway(code: Number? = definedExternally /* null */, lastStreamID: Number? = definedExternally /* null */, opaqueData: Uint32Array = definedExternally /* null */)
    open fun goaway(code: Number? = definedExternally /* null */, lastStreamID: Number? = definedExternally /* null */, opaqueData: Int8Array = definedExternally /* null */)
    open fun goaway(code: Number? = definedExternally /* null */, lastStreamID: Number? = definedExternally /* null */, opaqueData: Int16Array = definedExternally /* null */)
    open fun goaway(code: Number? = definedExternally /* null */, lastStreamID: Number? = definedExternally /* null */, opaqueData: Int32Array = definedExternally /* null */)
    open fun goaway(code: Number? = definedExternally /* null */, lastStreamID: Number? = definedExternally /* null */, opaqueData: Float32Array = definedExternally /* null */)
    open fun goaway(code: Number? = definedExternally /* null */, lastStreamID: Number? = definedExternally /* null */, opaqueData: Float64Array = definedExternally /* null */)
    open fun goaway(code: Number? = definedExternally /* null */, lastStreamID: Number? = definedExternally /* null */, opaqueData: DataView = definedExternally /* null */)
    open var localSettings: Settings
    open var originSet: Array<String>
    open var pendingSettingsAck: Boolean
    open fun ping(callback: (err: Error?, duration: Number, payload: Buffer) -> Unit): Boolean
    open fun ping(payload: Uint8Array, callback: (err: Error?, duration: Number, payload: Buffer) -> Unit): Boolean
    open fun ping(payload: Uint8ClampedArray, callback: (err: Error?, duration: Number, payload: Buffer) -> Unit): Boolean
    open fun ping(payload: Uint16Array, callback: (err: Error?, duration: Number, payload: Buffer) -> Unit): Boolean
    open fun ping(payload: Uint32Array, callback: (err: Error?, duration: Number, payload: Buffer) -> Unit): Boolean
    open fun ping(payload: Int8Array, callback: (err: Error?, duration: Number, payload: Buffer) -> Unit): Boolean
    open fun ping(payload: Int16Array, callback: (err: Error?, duration: Number, payload: Buffer) -> Unit): Boolean
    open fun ping(payload: Int32Array, callback: (err: Error?, duration: Number, payload: Buffer) -> Unit): Boolean
    open fun ping(payload: Float32Array, callback: (err: Error?, duration: Number, payload: Buffer) -> Unit): Boolean
    open fun ping(payload: Float64Array, callback: (err: Error?, duration: Number, payload: Buffer) -> Unit): Boolean
    open fun ping(payload: DataView, callback: (err: Error?, duration: Number, payload: Buffer) -> Unit): Boolean
    open fun ref()
    open var remoteSettings: Settings
    open fun setTimeout(msecs: Number, callback: (() -> Unit)? = definedExternally /* null */)
    open var socket: dynamic /* net.Socket | tls.TLSSocket */
    open var state: SessionState
    open fun settings(settings: Settings)
    open var type: Number
    open fun unref()
    open fun addListener(event: String /* "close" */, listener: () -> Unit): Http2Session /* this */
    open fun addListener(event: String /* "error" */, listener: (err: Error) -> Unit): Http2Session /* this */
    open fun addListener(event: String /* "frameError" */, listener: (frameType: Number, errorCode: Number, streamID: Number) -> Unit): Http2Session /* this */
    open fun addListener(event: String /* "goaway" */, listener: (errorCode: Number, lastStreamID: Number, opaqueData: Buffer) -> Unit): Http2Session /* this */
    open fun addListener(event: String /* "localSettings" */, listener: (settings: Settings) -> Unit): Http2Session /* this */
    open fun addListener(event: String /* "ping" */, listener: () -> Unit): Http2Session /* this */
    open fun addListener(event: String /* "remoteSettings" */, listener: (settings: Settings) -> Unit): Http2Session /* this */
    open fun addListener(event: String /* "timeout" */, listener: () -> Unit): Http2Session /* this */
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Http2Session /* this */
    open fun addListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2Session /* this */
    open fun emit(event: String /* "close" */): Boolean
    open fun emit(event: String /* "error" */, err: Error): Boolean
    open fun emit(event: String /* "frameError" */, frameType: Number, errorCode: Number, streamID: Number): Boolean
    open fun emit(event: String /* "goaway" */, errorCode: Number, lastStreamID: Number, opaqueData: Buffer): Boolean
    open fun emit(event: String /* "localSettings" */, settings: Settings): Boolean
    open fun emit(event: String /* "ping" */): Boolean
    open fun emit(event: String /* "remoteSettings" */, settings: Settings): Boolean
    open fun emit(event: String /* "timeout" */): Boolean
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun on(event: String /* "close" */, listener: () -> Unit): Http2Session /* this */
    open fun on(event: String /* "error" */, listener: (err: Error) -> Unit): Http2Session /* this */
    open fun on(event: String /* "frameError" */, listener: (frameType: Number, errorCode: Number, streamID: Number) -> Unit): Http2Session /* this */
    open fun on(event: String /* "goaway" */, listener: (errorCode: Number, lastStreamID: Number, opaqueData: Buffer) -> Unit): Http2Session /* this */
    open fun on(event: String /* "localSettings" */, listener: (settings: Settings) -> Unit): Http2Session /* this */
    open fun on(event: String /* "ping" */, listener: () -> Unit): Http2Session /* this */
    open fun on(event: String /* "remoteSettings" */, listener: (settings: Settings) -> Unit): Http2Session /* this */
    open fun on(event: String /* "timeout" */, listener: () -> Unit): Http2Session /* this */
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): Http2Session /* this */
    open fun on(event: Any, listener: (args: Array<Any>) -> Unit): Http2Session /* this */
    open fun once(event: String /* "close" */, listener: () -> Unit): Http2Session /* this */
    open fun once(event: String /* "error" */, listener: (err: Error) -> Unit): Http2Session /* this */
    open fun once(event: String /* "frameError" */, listener: (frameType: Number, errorCode: Number, streamID: Number) -> Unit): Http2Session /* this */
    open fun once(event: String /* "goaway" */, listener: (errorCode: Number, lastStreamID: Number, opaqueData: Buffer) -> Unit): Http2Session /* this */
    open fun once(event: String /* "localSettings" */, listener: (settings: Settings) -> Unit): Http2Session /* this */
    open fun once(event: String /* "ping" */, listener: () -> Unit): Http2Session /* this */
    open fun once(event: String /* "remoteSettings" */, listener: (settings: Settings) -> Unit): Http2Session /* this */
    open fun once(event: String /* "timeout" */, listener: () -> Unit): Http2Session /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): Http2Session /* this */
    open fun once(event: Any, listener: (args: Array<Any>) -> Unit): Http2Session /* this */
    open fun prependListener(event: String /* "close" */, listener: () -> Unit): Http2Session /* this */
    open fun prependListener(event: String /* "error" */, listener: (err: Error) -> Unit): Http2Session /* this */
    open fun prependListener(event: String /* "frameError" */, listener: (frameType: Number, errorCode: Number, streamID: Number) -> Unit): Http2Session /* this */
    open fun prependListener(event: String /* "goaway" */, listener: (errorCode: Number, lastStreamID: Number, opaqueData: Buffer) -> Unit): Http2Session /* this */
    open fun prependListener(event: String /* "localSettings" */, listener: (settings: Settings) -> Unit): Http2Session /* this */
    open fun prependListener(event: String /* "ping" */, listener: () -> Unit): Http2Session /* this */
    open fun prependListener(event: String /* "remoteSettings" */, listener: (settings: Settings) -> Unit): Http2Session /* this */
    open fun prependListener(event: String /* "timeout" */, listener: () -> Unit): Http2Session /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Http2Session /* this */
    open fun prependListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2Session /* this */
    open fun prependOnceListener(event: String /* "close" */, listener: () -> Unit): Http2Session /* this */
    open fun prependOnceListener(event: String /* "error" */, listener: (err: Error) -> Unit): Http2Session /* this */
    open fun prependOnceListener(event: String /* "frameError" */, listener: (frameType: Number, errorCode: Number, streamID: Number) -> Unit): Http2Session /* this */
    open fun prependOnceListener(event: String /* "goaway" */, listener: (errorCode: Number, lastStreamID: Number, opaqueData: Buffer) -> Unit): Http2Session /* this */
    open fun prependOnceListener(event: String /* "localSettings" */, listener: (settings: Settings) -> Unit): Http2Session /* this */
    open fun prependOnceListener(event: String /* "ping" */, listener: () -> Unit): Http2Session /* this */
    open fun prependOnceListener(event: String /* "remoteSettings" */, listener: (settings: Settings) -> Unit): Http2Session /* this */
    open fun prependOnceListener(event: String /* "timeout" */, listener: () -> Unit): Http2Session /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Http2Session /* this */
    open fun prependOnceListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2Session /* this */
    open fun goaway()
}

external open class ClientHttp2Session : Http2Session {
    open fun request(headers: OutgoingHttpHeaders? = definedExternally /* null */, options: ClientSessionRequestOptions? = definedExternally /* null */): ClientHttp2Stream
    open fun addListener(event: String /* "altsvc" */, listener: (alt: String, origin: String, stream: Number) -> Unit): ClientHttp2Session /* this */
    open fun addListener(event: String /* "origin" */, listener: (origins: Array<String>) -> Unit): ClientHttp2Session /* this */
    open fun addListener(event: String /* "connect" */, listener: (session: ClientHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit): ClientHttp2Session /* this */
    open fun addListener(event: String /* "stream" */, listener: (stream: ClientHttp2Stream, headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Session /* this */
    override fun addListener(event: String, listener: (args: Array<Any>) -> Unit): ClientHttp2Session /* this */
    override fun addListener(event: Any, listener: (args: Array<Any>) -> Unit): ClientHttp2Session /* this */
    open fun emit(event: String /* "altsvc" */, alt: String, origin: String, stream: Number): Boolean
    override fun emit(event: String /* "origin" */, origins: Array<String>): Boolean
    open fun emit(event: String /* "connect" */, session: ClientHttp2Session, socket: net.Socket): Boolean
    open fun emit(event: String /* "connect" */, session: ClientHttp2Session, socket: tls.TLSSocket): Boolean
    open fun emit(event: String /* "stream" */, stream: ClientHttp2Stream, headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number): Boolean
    override fun emit(event: String, vararg args: Any): Boolean
    override fun emit(event: Any, vararg args: Any): Boolean
    open fun on(event: String /* "altsvc" */, listener: (alt: String, origin: String, stream: Number) -> Unit): ClientHttp2Session /* this */
    open fun on(event: String /* "origin" */, listener: (origins: Array<String>) -> Unit): ClientHttp2Session /* this */
    open fun on(event: String /* "connect" */, listener: (session: ClientHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit): ClientHttp2Session /* this */
    open fun on(event: String /* "stream" */, listener: (stream: ClientHttp2Stream, headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Session /* this */
    override fun on(event: String, listener: (args: Array<Any>) -> Unit): ClientHttp2Session /* this */
    override fun on(event: Any, listener: (args: Array<Any>) -> Unit): ClientHttp2Session /* this */
    open fun once(event: String /* "altsvc" */, listener: (alt: String, origin: String, stream: Number) -> Unit): ClientHttp2Session /* this */
    open fun once(event: String /* "origin" */, listener: (origins: Array<String>) -> Unit): ClientHttp2Session /* this */
    open fun once(event: String /* "connect" */, listener: (session: ClientHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit): ClientHttp2Session /* this */
    open fun once(event: String /* "stream" */, listener: (stream: ClientHttp2Stream, headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Session /* this */
    override fun once(event: String, listener: (args: Array<Any>) -> Unit): ClientHttp2Session /* this */
    override fun once(event: Any, listener: (args: Array<Any>) -> Unit): ClientHttp2Session /* this */
    open fun prependListener(event: String /* "altsvc" */, listener: (alt: String, origin: String, stream: Number) -> Unit): ClientHttp2Session /* this */
    open fun prependListener(event: String /* "origin" */, listener: (origins: Array<String>) -> Unit): ClientHttp2Session /* this */
    open fun prependListener(event: String /* "connect" */, listener: (session: ClientHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit): ClientHttp2Session /* this */
    open fun prependListener(event: String /* "stream" */, listener: (stream: ClientHttp2Stream, headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Session /* this */
    override fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): ClientHttp2Session /* this */
    override fun prependListener(event: Any, listener: (args: Array<Any>) -> Unit): ClientHttp2Session /* this */
    open fun prependOnceListener(event: String /* "altsvc" */, listener: (alt: String, origin: String, stream: Number) -> Unit): ClientHttp2Session /* this */
    open fun prependOnceListener(event: String /* "origin" */, listener: (origins: Array<String>) -> Unit): ClientHttp2Session /* this */
    open fun prependOnceListener(event: String /* "connect" */, listener: (session: ClientHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit): ClientHttp2Session /* this */
    open fun prependOnceListener(event: String /* "stream" */, listener: (stream: ClientHttp2Stream, headers: IncomingHttpHeaders /* IncomingHttpHeaders & IncomingHttpStatusHeader */, flags: Number) -> Unit): ClientHttp2Session /* this */
    override fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): ClientHttp2Session /* this */
    override fun prependOnceListener(event: Any, listener: (args: Array<Any>) -> Unit): ClientHttp2Session /* this */
}

external interface AlternativeServiceOptions {
    var origin: dynamic /* Number | String | url.URL */
}

external interface `T$0` {
    var origin: String
}

external open class ServerHttp2Session : Http2Session {
    open fun altsvc(alt: String, originOrStream: Number)
    open fun altsvc(alt: String, originOrStream: String)
    open fun altsvc(alt: String, originOrStream: url.URL)
    open fun altsvc(alt: String, originOrStream: AlternativeServiceOptions)
    open fun origin(vararg args: String)
    open fun origin(vararg args: url.URL)
    open fun origin(vararg args: `T$0`)
    open var server: dynamic /* Http2Server | Http2SecureServer */
    open fun addListener(event: String /* "connect" */, listener: (session: ServerHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit): ServerHttp2Session /* this */
    open fun addListener(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): ServerHttp2Session /* this */
    override fun addListener(event: String, listener: (args: Array<Any>) -> Unit): ServerHttp2Session /* this */
    override fun addListener(event: Any, listener: (args: Array<Any>) -> Unit): ServerHttp2Session /* this */
    open fun emit(event: String /* "connect" */, session: ServerHttp2Session, socket: net.Socket): Boolean
    open fun emit(event: String /* "connect" */, session: ServerHttp2Session, socket: tls.TLSSocket): Boolean
    open fun emit(event: String /* "stream" */, stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number): Boolean
    override fun emit(event: String, vararg args: Any): Boolean
    override fun emit(event: Any, vararg args: Any): Boolean
    open fun on(event: String /* "connect" */, listener: (session: ServerHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit): ServerHttp2Session /* this */
    open fun on(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): ServerHttp2Session /* this */
    override fun on(event: String, listener: (args: Array<Any>) -> Unit): ServerHttp2Session /* this */
    override fun on(event: Any, listener: (args: Array<Any>) -> Unit): ServerHttp2Session /* this */
    open fun once(event: String /* "connect" */, listener: (session: ServerHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit): ServerHttp2Session /* this */
    open fun once(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): ServerHttp2Session /* this */
    override fun once(event: String, listener: (args: Array<Any>) -> Unit): ServerHttp2Session /* this */
    override fun once(event: Any, listener: (args: Array<Any>) -> Unit): ServerHttp2Session /* this */
    open fun prependListener(event: String /* "connect" */, listener: (session: ServerHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit): ServerHttp2Session /* this */
    open fun prependListener(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): ServerHttp2Session /* this */
    override fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): ServerHttp2Session /* this */
    override fun prependListener(event: Any, listener: (args: Array<Any>) -> Unit): ServerHttp2Session /* this */
    open fun prependOnceListener(event: String /* "connect" */, listener: (session: ServerHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit): ServerHttp2Session /* this */
    open fun prependOnceListener(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): ServerHttp2Session /* this */
    override fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): ServerHttp2Session /* this */
    override fun prependOnceListener(event: Any, listener: (args: Array<Any>) -> Unit): ServerHttp2Session /* this */
}

external interface SessionOptions {
    var maxDeflateDynamicTableSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxSessionMemory: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxHeaderListPairs: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxOutstandingPings: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxSendHeaderBlockLength: Number?
        get() = definedExternally
        set(value) = definedExternally
    var paddingStrategy: Number?
        get() = definedExternally
        set(value) = definedExternally
    var peerMaxConcurrentStreams: Number?
        get() = definedExternally
        set(value) = definedExternally
    var selectPadding: ((frameLen: Number, maxFrameLen: Number) -> Number)?
        get() = definedExternally
        set(value) = definedExternally
    var settings: Settings?
        get() = definedExternally
        set(value) = definedExternally
    var createConnection: ((authority: url.URL, option: SessionOptions) -> stream.Duplex)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ClientSessionOptions : SessionOptions {
    var maxReservedRemoteStreams: Number?
        get() = definedExternally
        set(value) = definedExternally
    override var createConnection: ((authority: url.URL, option: SessionOptions) -> stream.Duplex)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ServerSessionOptions : SessionOptions {
    var Http1IncomingMessage: Any?
        get() = definedExternally
        set(value) = definedExternally
    var Http1ServerResponse: Any?
        get() = definedExternally
        set(value) = definedExternally
    var Http2ServerRequest: Any?
        get() = definedExternally
        set(value) = definedExternally
    var Http2ServerResponse: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SecureClientSessionOptions : ClientSessionOptions, tls.ConnectionOptions

external interface SecureServerSessionOptions : ServerSessionOptions, tls.TlsOptions

external interface ServerOptions : ServerSessionOptions

external interface SecureServerOptions : SecureServerSessionOptions {
    var allowHTTP1: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var origins: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
}

external open class Http2Server : net.Server {
    open fun addListener(event: String /* "checkContinue" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2Server /* this */
    open fun addListener(event: String /* "request" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2Server /* this */
    open fun addListener(event: String /* "session" */, listener: (session: ServerHttp2Session) -> Unit): Http2Server /* this */
    open fun addListener(event: String /* "sessionError" */, listener: (err: Error) -> Unit): Http2Server /* this */
    open fun addListener(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): Http2Server /* this */
    open fun addListener(event: String /* "timeout" */, listener: () -> Unit): Http2Server /* this */
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Http2Server /* this */
    open fun addListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2Server /* this */
    open fun emit(event: String /* "checkContinue" */, request: Http2ServerRequest, response: Http2ServerResponse): Boolean
    open fun emit(event: String /* "request" */, request: Http2ServerRequest, response: Http2ServerResponse): Boolean
    open fun emit(event: String /* "session" */, session: ServerHttp2Session): Boolean
    open fun emit(event: String /* "sessionError" */, err: Error): Boolean
    open fun emit(event: String /* "stream" */, stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number): Boolean
    open fun emit(event: String /* "timeout" */): Boolean
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun on(event: String /* "checkContinue" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2Server /* this */
    open fun on(event: String /* "request" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2Server /* this */
    open fun on(event: String /* "session" */, listener: (session: ServerHttp2Session) -> Unit): Http2Server /* this */
    open fun on(event: String /* "sessionError" */, listener: (err: Error) -> Unit): Http2Server /* this */
    open fun on(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): Http2Server /* this */
    open fun on(event: String /* "timeout" */, listener: () -> Unit): Http2Server /* this */
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): Http2Server /* this */
    open fun on(event: Any, listener: (args: Array<Any>) -> Unit): Http2Server /* this */
    open fun once(event: String /* "checkContinue" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2Server /* this */
    open fun once(event: String /* "request" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2Server /* this */
    open fun once(event: String /* "session" */, listener: (session: ServerHttp2Session) -> Unit): Http2Server /* this */
    open fun once(event: String /* "sessionError" */, listener: (err: Error) -> Unit): Http2Server /* this */
    open fun once(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): Http2Server /* this */
    open fun once(event: String /* "timeout" */, listener: () -> Unit): Http2Server /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): Http2Server /* this */
    open fun once(event: Any, listener: (args: Array<Any>) -> Unit): Http2Server /* this */
    open fun prependListener(event: String /* "checkContinue" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2Server /* this */
    open fun prependListener(event: String /* "request" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2Server /* this */
    open fun prependListener(event: String /* "session" */, listener: (session: ServerHttp2Session) -> Unit): Http2Server /* this */
    open fun prependListener(event: String /* "sessionError" */, listener: (err: Error) -> Unit): Http2Server /* this */
    open fun prependListener(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): Http2Server /* this */
    open fun prependListener(event: String /* "timeout" */, listener: () -> Unit): Http2Server /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Http2Server /* this */
    open fun prependListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2Server /* this */
    open fun prependOnceListener(event: String /* "checkContinue" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2Server /* this */
    open fun prependOnceListener(event: String /* "request" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2Server /* this */
    open fun prependOnceListener(event: String /* "session" */, listener: (session: ServerHttp2Session) -> Unit): Http2Server /* this */
    open fun prependOnceListener(event: String /* "sessionError" */, listener: (err: Error) -> Unit): Http2Server /* this */
    open fun prependOnceListener(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): Http2Server /* this */
    open fun prependOnceListener(event: String /* "timeout" */, listener: () -> Unit): Http2Server /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Http2Server /* this */
    open fun prependOnceListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2Server /* this */
    open fun setTimeout(msec: Number? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): Http2Server /* this */
}

external open class Http2SecureServer : tls.Server {
    open fun addListener(event: String /* "checkContinue" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2SecureServer /* this */
    open fun addListener(event: String /* "request" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2SecureServer /* this */
    open fun addListener(event: String /* "session" */, listener: (session: ServerHttp2Session) -> Unit): Http2SecureServer /* this */
    open fun addListener(event: String /* "sessionError" */, listener: (err: Error) -> Unit): Http2SecureServer /* this */
    open fun addListener(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): Http2SecureServer /* this */
    open fun addListener(event: String /* "timeout" */, listener: () -> Unit): Http2SecureServer /* this */
    open fun addListener(event: String /* "unknownProtocol" */, listener: (socket: tls.TLSSocket) -> Unit): Http2SecureServer /* this */
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Http2SecureServer /* this */
    open fun addListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2SecureServer /* this */
    open fun emit(event: String /* "checkContinue" */, request: Http2ServerRequest, response: Http2ServerResponse): Boolean
    open fun emit(event: String /* "request" */, request: Http2ServerRequest, response: Http2ServerResponse): Boolean
    open fun emit(event: String /* "session" */, session: ServerHttp2Session): Boolean
    open fun emit(event: String /* "sessionError" */, err: Error): Boolean
    open fun emit(event: String /* "stream" */, stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number): Boolean
    open fun emit(event: String /* "timeout" */): Boolean
    open fun emit(event: String /* "unknownProtocol" */, socket: tls.TLSSocket): Boolean
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun on(event: String /* "checkContinue" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2SecureServer /* this */
    open fun on(event: String /* "request" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2SecureServer /* this */
    open fun on(event: String /* "session" */, listener: (session: ServerHttp2Session) -> Unit): Http2SecureServer /* this */
    open fun on(event: String /* "sessionError" */, listener: (err: Error) -> Unit): Http2SecureServer /* this */
    open fun on(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): Http2SecureServer /* this */
    open fun on(event: String /* "timeout" */, listener: () -> Unit): Http2SecureServer /* this */
    open fun on(event: String /* "unknownProtocol" */, listener: (socket: tls.TLSSocket) -> Unit): Http2SecureServer /* this */
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): Http2SecureServer /* this */
    open fun on(event: Any, listener: (args: Array<Any>) -> Unit): Http2SecureServer /* this */
    open fun once(event: String /* "checkContinue" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2SecureServer /* this */
    open fun once(event: String /* "request" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2SecureServer /* this */
    open fun once(event: String /* "session" */, listener: (session: ServerHttp2Session) -> Unit): Http2SecureServer /* this */
    open fun once(event: String /* "sessionError" */, listener: (err: Error) -> Unit): Http2SecureServer /* this */
    open fun once(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): Http2SecureServer /* this */
    open fun once(event: String /* "timeout" */, listener: () -> Unit): Http2SecureServer /* this */
    open fun once(event: String /* "unknownProtocol" */, listener: (socket: tls.TLSSocket) -> Unit): Http2SecureServer /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): Http2SecureServer /* this */
    open fun once(event: Any, listener: (args: Array<Any>) -> Unit): Http2SecureServer /* this */
    open fun prependListener(event: String /* "checkContinue" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2SecureServer /* this */
    open fun prependListener(event: String /* "request" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2SecureServer /* this */
    open fun prependListener(event: String /* "session" */, listener: (session: ServerHttp2Session) -> Unit): Http2SecureServer /* this */
    open fun prependListener(event: String /* "sessionError" */, listener: (err: Error) -> Unit): Http2SecureServer /* this */
    open fun prependListener(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): Http2SecureServer /* this */
    open fun prependListener(event: String /* "timeout" */, listener: () -> Unit): Http2SecureServer /* this */
    open fun prependListener(event: String /* "unknownProtocol" */, listener: (socket: tls.TLSSocket) -> Unit): Http2SecureServer /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Http2SecureServer /* this */
    open fun prependListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2SecureServer /* this */
    open fun prependOnceListener(event: String /* "checkContinue" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2SecureServer /* this */
    open fun prependOnceListener(event: String /* "request" */, listener: (request: Http2ServerRequest, response: Http2ServerResponse) -> Unit): Http2SecureServer /* this */
    open fun prependOnceListener(event: String /* "session" */, listener: (session: ServerHttp2Session) -> Unit): Http2SecureServer /* this */
    open fun prependOnceListener(event: String /* "sessionError" */, listener: (err: Error) -> Unit): Http2SecureServer /* this */
    open fun prependOnceListener(event: String /* "stream" */, listener: (stream: ServerHttp2Stream, headers: IncomingHttpHeaders, flags: Number) -> Unit): Http2SecureServer /* this */
    open fun prependOnceListener(event: String /* "timeout" */, listener: () -> Unit): Http2SecureServer /* this */
    open fun prependOnceListener(event: String /* "unknownProtocol" */, listener: (socket: tls.TLSSocket) -> Unit): Http2SecureServer /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Http2SecureServer /* this */
    open fun prependOnceListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2SecureServer /* this */
    open fun setTimeout(msec: Number? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): Http2SecureServer /* this */
}

external open class Http2ServerRequest(stream: ServerHttp2Stream, headers: IncomingHttpHeaders, options: stream.ReadableOptions, rawHeaders: Array<String>) : stream.Readable {
    open var aborted: Boolean
    open var authority: String
    open var headers: IncomingHttpHeaders
    open var httpVersion: String
    open var method: String
    open var rawHeaders: Array<String>
    open var rawTrailers: Array<String>
    open var scheme: String
    open fun setTimeout(msecs: Number, callback: (() -> Unit)? = definedExternally /* null */)
    open var socket: dynamic /* net.Socket | tls.TLSSocket */
    open var stream: ServerHttp2Stream
    open var trailers: IncomingHttpHeaders
    open var url: String
    open fun read(size: Number? = definedExternally /* null */): dynamic /* Buffer | String | Nothing? */
    open fun addListener(event: String /* "aborted" */, listener: (hadError: Boolean, code: Number) -> Unit): Http2ServerRequest /* this */
    open fun addListener(event: String /* "close" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun addListener(event: String /* "data" */, listener: (chunk: dynamic /* Buffer | String */) -> Unit): Http2ServerRequest /* this */
    open fun addListener(event: String /* "end" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun addListener(event: String /* "readable" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun addListener(event: String /* "error" */, listener: (err: Error) -> Unit): Http2ServerRequest /* this */
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Http2ServerRequest /* this */
    open fun addListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2ServerRequest /* this */
    open fun emit(event: String /* "aborted" */, hadError: Boolean, code: Number): Boolean
    open fun emit(event: String /* "close" */): Boolean
    open fun emit(event: String /* "data" */, chunk: Buffer): Boolean
    open fun emit(event: String /* "data" */, chunk: String): Boolean
    open fun emit(event: String /* "end" */): Boolean
    open fun emit(event: String /* "readable" */): Boolean
    open fun emit(event: String /* "error" */, err: Error): Boolean
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun on(event: String /* "aborted" */, listener: (hadError: Boolean, code: Number) -> Unit): Http2ServerRequest /* this */
    open fun on(event: String /* "close" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun on(event: String /* "data" */, listener: (chunk: dynamic /* Buffer | String */) -> Unit): Http2ServerRequest /* this */
    open fun on(event: String /* "end" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun on(event: String /* "readable" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun on(event: String /* "error" */, listener: (err: Error) -> Unit): Http2ServerRequest /* this */
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): Http2ServerRequest /* this */
    open fun on(event: Any, listener: (args: Array<Any>) -> Unit): Http2ServerRequest /* this */
    open fun once(event: String /* "aborted" */, listener: (hadError: Boolean, code: Number) -> Unit): Http2ServerRequest /* this */
    open fun once(event: String /* "close" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun once(event: String /* "data" */, listener: (chunk: dynamic /* Buffer | String */) -> Unit): Http2ServerRequest /* this */
    open fun once(event: String /* "end" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun once(event: String /* "readable" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun once(event: String /* "error" */, listener: (err: Error) -> Unit): Http2ServerRequest /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): Http2ServerRequest /* this */
    open fun once(event: Any, listener: (args: Array<Any>) -> Unit): Http2ServerRequest /* this */
    open fun prependListener(event: String /* "aborted" */, listener: (hadError: Boolean, code: Number) -> Unit): Http2ServerRequest /* this */
    open fun prependListener(event: String /* "close" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun prependListener(event: String /* "data" */, listener: (chunk: dynamic /* Buffer | String */) -> Unit): Http2ServerRequest /* this */
    open fun prependListener(event: String /* "end" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun prependListener(event: String /* "readable" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun prependListener(event: String /* "error" */, listener: (err: Error) -> Unit): Http2ServerRequest /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Http2ServerRequest /* this */
    open fun prependListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2ServerRequest /* this */
    open fun prependOnceListener(event: String /* "aborted" */, listener: (hadError: Boolean, code: Number) -> Unit): Http2ServerRequest /* this */
    open fun prependOnceListener(event: String /* "close" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun prependOnceListener(event: String /* "data" */, listener: (chunk: dynamic /* Buffer | String */) -> Unit): Http2ServerRequest /* this */
    open fun prependOnceListener(event: String /* "end" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun prependOnceListener(event: String /* "readable" */, listener: () -> Unit): Http2ServerRequest /* this */
    open fun prependOnceListener(event: String /* "error" */, listener: (err: Error) -> Unit): Http2ServerRequest /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Http2ServerRequest /* this */
    open fun prependOnceListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2ServerRequest /* this */
}

external open class Http2ServerResponse(stream: ServerHttp2Stream) : stream.Stream {
    open fun addTrailers(trailers: OutgoingHttpHeaders)
    open var connection: dynamic /* net.Socket | tls.TLSSocket */
    open fun end(callback: (() -> Unit)? = definedExternally /* null */)
    open fun end(data: String, callback: (() -> Unit)? = definedExternally /* null */)
    open fun end(data: Uint8Array, callback: (() -> Unit)? = definedExternally /* null */)
    open fun end(data: String, encoding: String, callback: (() -> Unit)? = definedExternally /* null */)
    open fun end(data: Uint8Array, encoding: String, callback: (() -> Unit)? = definedExternally /* null */)
    open var finished: Boolean
    open fun getHeader(name: String): String
    open fun getHeaderNames(): Array<String>
    open fun getHeaders(): OutgoingHttpHeaders
    open fun hasHeader(name: String): Boolean
    open var headersSent: Boolean
    open fun removeHeader(name: String)
    open var sendDate: Boolean
    open fun setHeader(name: String, value: Number)
    open fun setHeader(name: String, value: String)
    open fun setHeader(name: String, value: Array<String>)
    open fun setTimeout(msecs: Number, callback: (() -> Unit)? = definedExternally /* null */)
    open var socket: dynamic /* net.Socket | tls.TLSSocket */
    open var statusCode: Number
    open var statusMessage: String /* '' */
    open var stream: ServerHttp2Stream
    open fun write(chunk: String, callback: ((err: Error) -> Unit)? = definedExternally /* null */): Boolean
    open fun write(chunk: Uint8Array, callback: ((err: Error) -> Unit)? = definedExternally /* null */): Boolean
    open fun write(chunk: String, encoding: String, callback: ((err: Error) -> Unit)? = definedExternally /* null */): Boolean
    open fun write(chunk: Uint8Array, encoding: String, callback: ((err: Error) -> Unit)? = definedExternally /* null */): Boolean
    open fun writeContinue()
    open fun writeHead(statusCode: Number, headers: OutgoingHttpHeaders? = definedExternally /* null */): Http2ServerResponse /* this */
    open fun writeHead(statusCode: Number, statusMessage: String, headers: OutgoingHttpHeaders? = definedExternally /* null */): Http2ServerResponse /* this */
    open fun createPushResponse(headers: OutgoingHttpHeaders, callback: (err: Error?, res: Http2ServerResponse) -> Unit)
    open fun addListener(event: String /* "close" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun addListener(event: String /* "drain" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun addListener(event: String /* "error" */, listener: (error: Error) -> Unit): Http2ServerResponse /* this */
    open fun addListener(event: String /* "finish" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun addListener(event: String /* "pipe" */, listener: (src: stream.Readable) -> Unit): Http2ServerResponse /* this */
    open fun addListener(event: String /* "unpipe" */, listener: (src: stream.Readable) -> Unit): Http2ServerResponse /* this */
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Http2ServerResponse /* this */
    open fun addListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2ServerResponse /* this */
    open fun emit(event: String /* "close" */): Boolean
    open fun emit(event: String /* "drain" */): Boolean
    open fun emit(event: String /* "error" */, error: Error): Boolean
    open fun emit(event: String /* "finish" */): Boolean
    open fun emit(event: String /* "pipe" */, src: stream.Readable): Boolean
    open fun emit(event: String /* "unpipe" */, src: stream.Readable): Boolean
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun on(event: String /* "close" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun on(event: String /* "drain" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun on(event: String /* "error" */, listener: (error: Error) -> Unit): Http2ServerResponse /* this */
    open fun on(event: String /* "finish" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun on(event: String /* "pipe" */, listener: (src: stream.Readable) -> Unit): Http2ServerResponse /* this */
    open fun on(event: String /* "unpipe" */, listener: (src: stream.Readable) -> Unit): Http2ServerResponse /* this */
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): Http2ServerResponse /* this */
    open fun on(event: Any, listener: (args: Array<Any>) -> Unit): Http2ServerResponse /* this */
    open fun once(event: String /* "close" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun once(event: String /* "drain" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun once(event: String /* "error" */, listener: (error: Error) -> Unit): Http2ServerResponse /* this */
    open fun once(event: String /* "finish" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun once(event: String /* "pipe" */, listener: (src: stream.Readable) -> Unit): Http2ServerResponse /* this */
    open fun once(event: String /* "unpipe" */, listener: (src: stream.Readable) -> Unit): Http2ServerResponse /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): Http2ServerResponse /* this */
    open fun once(event: Any, listener: (args: Array<Any>) -> Unit): Http2ServerResponse /* this */
    open fun prependListener(event: String /* "close" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun prependListener(event: String /* "drain" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun prependListener(event: String /* "error" */, listener: (error: Error) -> Unit): Http2ServerResponse /* this */
    open fun prependListener(event: String /* "finish" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun prependListener(event: String /* "pipe" */, listener: (src: stream.Readable) -> Unit): Http2ServerResponse /* this */
    open fun prependListener(event: String /* "unpipe" */, listener: (src: stream.Readable) -> Unit): Http2ServerResponse /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Http2ServerResponse /* this */
    open fun prependListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2ServerResponse /* this */
    open fun prependOnceListener(event: String /* "close" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun prependOnceListener(event: String /* "drain" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun prependOnceListener(event: String /* "error" */, listener: (error: Error) -> Unit): Http2ServerResponse /* this */
    open fun prependOnceListener(event: String /* "finish" */, listener: () -> Unit): Http2ServerResponse /* this */
    open fun prependOnceListener(event: String /* "pipe" */, listener: (src: stream.Readable) -> Unit): Http2ServerResponse /* this */
    open fun prependOnceListener(event: String /* "unpipe" */, listener: (src: stream.Readable) -> Unit): Http2ServerResponse /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Http2ServerResponse /* this */
    open fun prependOnceListener(event: Any, listener: (args: Array<Any>) -> Unit): Http2ServerResponse /* this */
}

external fun getDefaultSettings(): Settings

external fun getPackedSettings(settings: Settings): Buffer

external fun getUnpackedSettings(buf: Uint8Array): Settings

external fun createServer(onRequestHandler: ((request: Http2ServerRequest, response: Http2ServerResponse) -> Unit)? = definedExternally /* null */): Http2Server

external fun createServer(options: ServerOptions, onRequestHandler: ((request: Http2ServerRequest, response: Http2ServerResponse) -> Unit)? = definedExternally /* null */): Http2Server

external fun createSecureServer(onRequestHandler: ((request: Http2ServerRequest, response: Http2ServerResponse) -> Unit)? = definedExternally /* null */): Http2SecureServer

external fun createSecureServer(options: SecureServerOptions, onRequestHandler: ((request: Http2ServerRequest, response: Http2ServerResponse) -> Unit)? = definedExternally /* null */): Http2SecureServer

external fun connect(authority: String, listener: ((session: ClientHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit)? = definedExternally /* null */): ClientHttp2Session

external fun connect(authority: url.URL, listener: ((session: ClientHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit)? = definedExternally /* null */): ClientHttp2Session

external fun connect(authority: String, options: ClientSessionOptions? = definedExternally /* null */, listener: ((session: ClientHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit)? = definedExternally /* null */): ClientHttp2Session

external fun connect(authority: String, options: SecureClientSessionOptions? = definedExternally /* null */, listener: ((session: ClientHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit)? = definedExternally /* null */): ClientHttp2Session

external fun connect(authority: url.URL, options: ClientSessionOptions? = definedExternally /* null */, listener: ((session: ClientHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit)? = definedExternally /* null */): ClientHttp2Session

external fun connect(authority: url.URL, options: SecureClientSessionOptions? = definedExternally /* null */, listener: ((session: ClientHttp2Session, socket: dynamic /* net.Socket | tls.TLSSocket */) -> Unit)? = definedExternally /* null */): ClientHttp2Session