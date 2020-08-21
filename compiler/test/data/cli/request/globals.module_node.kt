@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

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

inline var Console.Console: NodeJS.ConsoleConstructor get() = this.asDynamic().Console; set(value) { this.asDynamic().Console = value }

/* extending interface from lib.dom.d.ts */
inline fun Console.assert(value: Any, message: String? = definedExternally /* null */, vararg optionalParams: Any) { this.asDynamic().assert(value, message, optionalParams) }

/* extending interface from lib.dom.d.ts */
inline fun Console.clear() { this.asDynamic().clear() }

/* extending interface from lib.dom.d.ts */
inline fun Console.count(label: String? = definedExternally /* null */) { this.asDynamic().count(label) }

/* extending interface from lib.dom.d.ts */
inline fun Console.countReset(label: String? = definedExternally /* null */) { this.asDynamic().countReset(label) }

/* extending interface from lib.dom.d.ts */
inline fun Console.debug(message: Any? = definedExternally /* null */, vararg optionalParams: Any) { this.asDynamic().debug(message, optionalParams) }

/* extending interface from lib.dom.d.ts */
inline fun Console.dir(obj: Any, options: NodeJS.InspectOptions = definedExternally /* null */) { this.asDynamic().dir(obj, options) }

/* extending interface from lib.dom.d.ts */
inline fun Console.dirxml(vararg data: Any) { this.asDynamic().dirxml(data) }

/* extending interface from lib.dom.d.ts */
inline fun Console.error(message: Any? = definedExternally /* null */, vararg optionalParams: Any) { this.asDynamic().error(message, optionalParams) }

/* extending interface from lib.dom.d.ts */
inline fun Console.group(vararg label: Any) { this.asDynamic().group(label) }

/* extending interface from lib.dom.d.ts */
inline fun Console.groupCollapsed(vararg label: Any) { this.asDynamic().groupCollapsed(label) }

/* extending interface from lib.dom.d.ts */
inline fun Console.groupEnd() { this.asDynamic().groupEnd() }

/* extending interface from lib.dom.d.ts */
inline fun Console.info(message: Any? = definedExternally /* null */, vararg optionalParams: Any) { this.asDynamic().info(message, optionalParams) }

/* extending interface from lib.dom.d.ts */
inline fun Console.log(message: Any? = definedExternally /* null */, vararg optionalParams: Any) { this.asDynamic().log(message, optionalParams) }

/* extending interface from lib.dom.d.ts */
inline fun Console.table(tabularData: Any, properties: Array<String>? = definedExternally /* null */) { this.asDynamic().table(tabularData, properties) }

/* extending interface from lib.dom.d.ts */
inline fun Console.time(label: String? = definedExternally /* null */) { this.asDynamic().time(label) }

/* extending interface from lib.dom.d.ts */
inline fun Console.timeEnd(label: String? = definedExternally /* null */) { this.asDynamic().timeEnd(label) }

/* extending interface from lib.dom.d.ts */
inline fun Console.timeLog(label: String? = definedExternally /* null */, vararg data: Any) { this.asDynamic().timeLog(label, data) }

/* extending interface from lib.dom.d.ts */
inline fun Console.trace(message: Any? = definedExternally /* null */, vararg optionalParams: Any) { this.asDynamic().trace(message, optionalParams) }

/* extending interface from lib.dom.d.ts */
inline fun Console.warn(message: Any? = definedExternally /* null */, vararg optionalParams: Any) { this.asDynamic().warn(message, optionalParams) }

/* extending interface from lib.dom.d.ts */
inline fun Console.markTimeline(label: String? = definedExternally /* null */) { this.asDynamic().markTimeline(label) }

/* extending interface from lib.dom.d.ts */
inline fun Console.profile(label: String? = definedExternally /* null */) { this.asDynamic().profile(label) }

/* extending interface from lib.dom.d.ts */
inline fun Console.profileEnd(label: String? = definedExternally /* null */) { this.asDynamic().profileEnd(label) }

/* extending interface from lib.dom.d.ts */
inline fun Console.timeStamp(label: String? = definedExternally /* null */) { this.asDynamic().timeStamp(label) }

/* extending interface from lib.dom.d.ts */
inline fun Console.timeline(label: String? = definedExternally /* null */) { this.asDynamic().timeline(label) }

/* extending interface from lib.dom.d.ts */
inline fun Console.timelineEnd(label: String? = definedExternally /* null */) { this.asDynamic().timelineEnd(label) }

inline var Error.stack: String? get() = this.asDynamic().stack; set(value) { this.asDynamic().stack = value }

/* extending interface from lib.es5.d.ts */
inline fun ErrorConstructor.captureStackTrace(targetObject: Any, constructorOpt: Function<*>? = definedExternally /* null */) { this.asDynamic().captureStackTrace(targetObject, constructorOpt) }

inline var ErrorConstructor.prepareStackTrace: ((err: Error, stackTraces: Array<NodeJS.CallSite>) -> Any)? get() = this.asDynamic().prepareStackTrace; set(value) { this.asDynamic().prepareStackTrace = value }

inline var ErrorConstructor.stackTraceLimit: Number get() = this.asDynamic().stackTraceLimit; set(value) { this.asDynamic().stackTraceLimit = value }

inline var SymbolConstructor.observable: Any get() = this.asDynamic().observable; set(value) { this.asDynamic().observable = value }

/* extending interface from lib.es5.d.ts */
inline fun String.trimLeft(): String = this.asDynamic().trimLeft()

/* extending interface from lib.es5.d.ts */
inline fun String.trimRight(): String = this.asDynamic().trimRight()

inline var ImportMeta.url: String get() = this.asDynamic().url; set(value) { this.asDynamic().url = value }

external var process: NodeJS.Process

external var global: NodeJS.Global

external var console: Console

external var __filename: String

external var __dirname: String

external fun setTimeout(callback: (args: Array<Any>) -> Unit, ms: Number, vararg args: Any): NodeJS.Timeout

external fun clearTimeout(timeoutId: NodeJS.Timeout)

external fun setInterval(callback: (args: Array<Any>) -> Unit, ms: Number, vararg args: Any): NodeJS.Timeout

external fun clearInterval(intervalId: NodeJS.Timeout)

external fun setImmediate(callback: (args: Array<Any>) -> Unit, vararg args: Any): NodeJS.Immediate

external fun clearImmediate(immediateId: NodeJS.Immediate)

external fun queueMicrotask(callback: () -> Unit)

external interface NodeRequireFunction {
    @nativeInvoke
    operator fun invoke(id: String): Any
}

external interface NodeRequire : NodeRequireFunction {
    var resolve: RequireResolve
    var cache: Any
    var extensions: NodeExtensions
    var main: NodeModule?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$0` {
    var paths: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RequireResolve {
    @nativeInvoke
    operator fun invoke(id: String, options: `T$0`? = definedExternally /* null */): String
    fun paths(request: String): Array<String>?
}

external interface NodeExtensions {
    var `.js`: (m: NodeModule, filename: String) -> Any
    var `.json`: (m: NodeModule, filename: String) -> Any
    var `.node`: (m: NodeModule, filename: String) -> Any
    @nativeGetter
    operator fun get(ext: String): ((m: NodeModule, filename: String) -> Any)?
    @nativeSetter
    operator fun set(ext: String, value: (m: NodeModule, filename: String) -> Any)
}

external var require: NodeRequire

external interface NodeModule {
    var exports: Any
    var require: NodeRequireFunction
    var id: String
    var filename: String
    var loaded: Boolean
    var parent: NodeModule?
        get() = definedExternally
        set(value) = definedExternally
    var children: Array<NodeModule>
    var paths: Array<String>
}

external var module: NodeModule

external var exports: Any

external open class Buffer(size: Number) : Uint8Array {
    constructor(array: Uint8Array)
    constructor(array: Array<Any>)
    constructor(buffer: Buffer)
    constructor(str: String, encoding: String)
    constructor(arrayBuffer: ArrayBuffer)
    constructor(arrayBuffer: SharedArrayBuffer)
    open fun write(string: String, encoding: String /* "ascii" */ = definedExternally /* null */): Number
    open fun write(string: String, encoding: String /* "utf8" */ = definedExternally /* null */): Number
    open fun write(string: String, encoding: String /* "utf-8" */ = definedExternally /* null */): Number
    open fun write(string: String, encoding: String /* "utf16le" */ = definedExternally /* null */): Number
    open fun write(string: String, encoding: String /* "ucs2" */ = definedExternally /* null */): Number
    open fun write(string: String, encoding: String /* "ucs-2" */ = definedExternally /* null */): Number
    open fun write(string: String, encoding: String /* "base64" */ = definedExternally /* null */): Number
    open fun write(string: String, encoding: String /* "latin1" */ = definedExternally /* null */): Number
    open fun write(string: String, encoding: String /* "binary" */ = definedExternally /* null */): Number
    open fun write(string: String, encoding: String /* "hex" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, encoding: String /* "ascii" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, encoding: String /* "utf8" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, encoding: String /* "utf-8" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, encoding: String /* "utf16le" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, encoding: String /* "ucs2" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, encoding: String /* "ucs-2" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, encoding: String /* "base64" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, encoding: String /* "latin1" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, encoding: String /* "binary" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, encoding: String /* "hex" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, length: Number, encoding: String /* "ascii" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, length: Number, encoding: String /* "utf8" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, length: Number, encoding: String /* "utf-8" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, length: Number, encoding: String /* "utf16le" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, length: Number, encoding: String /* "ucs2" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, length: Number, encoding: String /* "ucs-2" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, length: Number, encoding: String /* "base64" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, length: Number, encoding: String /* "latin1" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, length: Number, encoding: String /* "binary" */ = definedExternally /* null */): Number
    open fun write(string: String, offset: Number, length: Number, encoding: String /* "hex" */ = definedExternally /* null */): Number
    open fun toString(encoding: String? = definedExternally /* null */, start: Number? = definedExternally /* null */, end: Number? = definedExternally /* null */): String
    open fun toJSON(): `T$1`
    open fun equals(otherBuffer: Uint8Array): Boolean
    open fun compare(otherBuffer: Uint8Array, targetStart: Number? = definedExternally /* null */, targetEnd: Number? = definedExternally /* null */, sourceStart: Number? = definedExternally /* null */, sourceEnd: Number? = definedExternally /* null */): Number
    open fun copy(targetBuffer: Uint8Array, targetStart: Number? = definedExternally /* null */, sourceStart: Number? = definedExternally /* null */, sourceEnd: Number? = definedExternally /* null */): Number
    override fun slice(begin: Number?, end: Number?): Buffer
    open fun subarray(begin: Number? = definedExternally /* null */, end: Number? = definedExternally /* null */): Buffer
    open fun writeUIntLE(value: Number, offset: Number, byteLength: Number): Number
    open fun writeUIntBE(value: Number, offset: Number, byteLength: Number): Number
    open fun writeIntLE(value: Number, offset: Number, byteLength: Number): Number
    open fun writeIntBE(value: Number, offset: Number, byteLength: Number): Number
    open fun readUIntLE(offset: Number, byteLength: Number): Number
    open fun readUIntBE(offset: Number, byteLength: Number): Number
    open fun readIntLE(offset: Number, byteLength: Number): Number
    open fun readIntBE(offset: Number, byteLength: Number): Number
    open fun readUInt8(offset: Number): Number
    open fun readUInt16LE(offset: Number): Number
    open fun readUInt16BE(offset: Number): Number
    open fun readUInt32LE(offset: Number): Number
    open fun readUInt32BE(offset: Number): Number
    open fun readInt8(offset: Number): Number
    open fun readInt16LE(offset: Number): Number
    open fun readInt16BE(offset: Number): Number
    open fun readInt32LE(offset: Number): Number
    open fun readInt32BE(offset: Number): Number
    open fun readFloatLE(offset: Number): Number
    open fun readFloatBE(offset: Number): Number
    open fun readDoubleLE(offset: Number): Number
    open fun readDoubleBE(offset: Number): Number
    override fun reverse(): Buffer /* this */
    open fun swap16(): Buffer
    open fun swap32(): Buffer
    open fun swap64(): Buffer
    open fun writeUInt8(value: Number, offset: Number): Number
    open fun writeUInt16LE(value: Number, offset: Number): Number
    open fun writeUInt16BE(value: Number, offset: Number): Number
    open fun writeUInt32LE(value: Number, offset: Number): Number
    open fun writeUInt32BE(value: Number, offset: Number): Number
    open fun writeInt8(value: Number, offset: Number): Number
    open fun writeInt16LE(value: Number, offset: Number): Number
    open fun writeInt16BE(value: Number, offset: Number): Number
    open fun writeInt32LE(value: Number, offset: Number): Number
    open fun writeInt32BE(value: Number, offset: Number): Number
    open fun writeFloatLE(value: Number, offset: Number): Number
    open fun writeFloatBE(value: Number, offset: Number): Number
    open fun writeDoubleLE(value: Number, offset: Number): Number
    open fun writeDoubleBE(value: Number, offset: Number): Number
    open fun fill(value: String, offset: Number? = definedExternally /* null */, end: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Buffer /* this */
    open fun fill(value: Uint8Array, offset: Number? = definedExternally /* null */, end: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Buffer /* this */
    open fun fill(value: Number, offset: Number? = definedExternally /* null */, end: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Buffer /* this */
    open fun indexOf(value: String, byteOffset: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
    open fun indexOf(value: Number, byteOffset: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
    open fun indexOf(value: Uint8Array, byteOffset: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
    open fun lastIndexOf(value: String, byteOffset: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
    open fun lastIndexOf(value: Number, byteOffset: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
    open fun lastIndexOf(value: Uint8Array, byteOffset: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
    open fun entries(): IterableIterator<dynamic /* JsTuple<Number, Number> */>
    open fun includes(value: String, byteOffset: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Boolean
    open fun includes(value: Number, byteOffset: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Boolean
    open fun includes(value: Buffer, byteOffset: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Boolean
    open fun keys(): IterableIterator<Number>
    open fun values(): IterableIterator<Number>
    open fun write(string: String): Number
    open fun write(string: String, offset: Number): Number
    open fun write(string: String, offset: Number, length: Number): Number
    var constructor: Any

    companion object {
        fun from(arrayBuffer: ArrayBuffer, byteOffset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */): Buffer
        fun from(arrayBuffer: SharedArrayBuffer, byteOffset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */): Buffer
        fun from(data: Array<Number>): Buffer
        fun from(data: Uint8Array): Buffer
        fun from(str: String, encoding: String /* "ascii" */ = definedExternally /* null */): Buffer
        fun from(str: String, encoding: String /* "utf8" */ = definedExternally /* null */): Buffer
        fun from(str: String, encoding: String /* "utf-8" */ = definedExternally /* null */): Buffer
        fun from(str: String, encoding: String /* "utf16le" */ = definedExternally /* null */): Buffer
        fun from(str: String, encoding: String /* "ucs2" */ = definedExternally /* null */): Buffer
        fun from(str: String, encoding: String /* "ucs-2" */ = definedExternally /* null */): Buffer
        fun from(str: String, encoding: String /* "base64" */ = definedExternally /* null */): Buffer
        fun from(str: String, encoding: String /* "latin1" */ = definedExternally /* null */): Buffer
        fun from(str: String, encoding: String /* "binary" */ = definedExternally /* null */): Buffer
        fun from(str: String, encoding: String /* "hex" */ = definedExternally /* null */): Buffer
        fun of(vararg items: Number): Buffer
        fun isBuffer(obj: Any): Boolean
        fun isEncoding(encoding: String): Boolean
        fun byteLength(string: String, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: Uint8Array, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: Uint8ClampedArray, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: Uint16Array, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: Uint32Array, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: Int8Array, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: Int16Array, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: Int32Array, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: Float32Array, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: Float64Array, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: DataView, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: ArrayBuffer, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun byteLength(string: SharedArrayBuffer, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Number
        fun concat(list: Array<Uint8Array>, totalLength: Number? = definedExternally /* null */): Buffer
        fun compare(buf1: Uint8Array, buf2: Uint8Array): Number
        fun alloc(size: Number, fill: String? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Buffer
        fun alloc(size: Number, fill: Buffer? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Buffer
        fun alloc(size: Number, fill: Number? = definedExternally /* null */, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */): Buffer
        fun allocUnsafe(size: Number): Buffer
        fun allocUnsafeSlow(size: Number): Buffer
        var poolSize: Number
        fun from(str: String): Buffer
        fun alloc(size: Number): Buffer
    }
}

external interface `T$1` {
    var type: String /* 'Buffer' */
    var data: Array<Number>
}