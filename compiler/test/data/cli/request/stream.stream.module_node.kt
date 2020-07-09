@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package stream

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

external interface `T$0` {
    var end: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

@JsModule("stream")
external open class internal : events.EventEmitter {
    open fun <T : NodeJS.WritableStream> pipe(destination: T, options: `T$0`? = definedExternally /* null */): T

    companion object {
        fun finished(stream: NodeJS.ReadableStream, callback: (err: NodeJS.ErrnoException?) -> Unit): () -> Unit
        fun finished(stream: NodeJS.WritableStream, callback: (err: NodeJS.ErrnoException?) -> Unit): () -> Unit
        fun finished(stream: NodeJS.ReadWriteStream, callback: (err: NodeJS.ErrnoException?) -> Unit): () -> Unit
        fun <T : NodeJS.WritableStream> pipeline(stream1: NodeJS.ReadableStream, stream2: T, callback: ((err: NodeJS.ErrnoException?) -> Unit)? = definedExternally /* null */): T
        fun <T : NodeJS.WritableStream> pipeline(stream1: NodeJS.ReadableStream, stream2: NodeJS.ReadWriteStream, stream3: T, callback: ((err: NodeJS.ErrnoException?) -> Unit)? = definedExternally /* null */): T
        fun <T : NodeJS.WritableStream> pipeline(stream1: NodeJS.ReadableStream, stream2: NodeJS.ReadWriteStream, stream3: NodeJS.ReadWriteStream, stream4: T, callback: ((err: NodeJS.ErrnoException?) -> Unit)? = definedExternally /* null */): T
        fun <T : NodeJS.WritableStream> pipeline(stream1: NodeJS.ReadableStream, stream2: NodeJS.ReadWriteStream, stream3: NodeJS.ReadWriteStream, stream4: NodeJS.ReadWriteStream, stream5: T, callback: ((err: NodeJS.ErrnoException?) -> Unit)? = definedExternally /* null */): T
        fun pipeline(streams: Array<dynamic /* NodeJS.ReadableStream | NodeJS.WritableStream | NodeJS.ReadWriteStream */>, callback: ((err: NodeJS.ErrnoException?) -> Unit)? = definedExternally /* null */): NodeJS.WritableStream
        fun pipeline(stream1: NodeJS.ReadableStream, stream2: NodeJS.ReadWriteStream, vararg streams: NodeJS.ReadWriteStream): NodeJS.WritableStream
        fun pipeline(stream1: NodeJS.ReadableStream, stream2: NodeJS.ReadWriteStream, vararg streams: NodeJS.WritableStream): NodeJS.WritableStream
        fun pipeline(stream1: NodeJS.ReadableStream, stream2: NodeJS.ReadWriteStream, vararg streams: (err: NodeJS.ErrnoException?) -> Unit): NodeJS.WritableStream
        fun pipeline(stream1: NodeJS.ReadableStream, stream2: NodeJS.WritableStream, vararg streams: NodeJS.ReadWriteStream): NodeJS.WritableStream
        fun pipeline(stream1: NodeJS.ReadableStream, stream2: NodeJS.WritableStream, vararg streams: NodeJS.WritableStream): NodeJS.WritableStream
        fun pipeline(stream1: NodeJS.ReadableStream, stream2: NodeJS.WritableStream, vararg streams: (err: NodeJS.ErrnoException?) -> Unit): NodeJS.WritableStream
    }
}