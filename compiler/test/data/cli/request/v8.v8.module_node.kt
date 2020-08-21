@file:JsModule("v8")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package v8

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

external interface HeapSpaceInfo {
    var space_name: String
    var space_size: Number
    var space_used_size: Number
    var space_available_size: Number
    var physical_space_size: Number
}

external interface HeapInfo {
    var total_heap_size: Number
    var total_heap_size_executable: Number
    var total_physical_size: Number
    var total_available_size: Number
    var used_heap_size: Number
    var heap_size_limit: Number
    var malloced_memory: Number
    var peak_malloced_memory: Number
    var does_zap_garbage: dynamic /* 0 | 1 */
    var number_of_native_contexts: Number
    var number_of_detached_contexts: Number
}

external interface HeapCodeStatistics {
    var code_and_metadata_size: Number
    var bytecode_and_metadata_size: Number
    var external_script_source_size: Number
}

external fun cachedDataVersionTag(): Number

external fun getHeapStatistics(): HeapInfo

external fun getHeapSpaceStatistics(): Array<HeapSpaceInfo>

external fun setFlagsFromString(flags: String)

external fun getHeapSnapshot(): Readable

external fun writeHeapSnapshot(fileName: String? = definedExternally /* null */): String

external fun getHeapCodeStatistics(): HeapCodeStatistics

external open class Serializer {
    open fun writeHeader()
    open fun writeValue(`val`: Any): Boolean
    open fun releaseBuffer(): Buffer
    open fun transferArrayBuffer(id: Number, arrayBuffer: ArrayBuffer)
    open fun writeUint32(value: Number)
    open fun writeUint64(hi: Number, lo: Number)
    open fun writeDouble(value: Number)
    open fun writeRawBytes(buffer: Uint8Array)
    open fun writeRawBytes(buffer: Uint8ClampedArray)
    open fun writeRawBytes(buffer: Uint16Array)
    open fun writeRawBytes(buffer: Uint32Array)
    open fun writeRawBytes(buffer: Int8Array)
    open fun writeRawBytes(buffer: Int16Array)
    open fun writeRawBytes(buffer: Int32Array)
    open fun writeRawBytes(buffer: Float32Array)
    open fun writeRawBytes(buffer: Float64Array)
}

external open class DefaultSerializer : Serializer

external open class Deserializer {
    constructor(data: Uint8Array)
    constructor(data: Uint8ClampedArray)
    constructor(data: Uint16Array)
    constructor(data: Uint32Array)
    constructor(data: Int8Array)
    constructor(data: Int16Array)
    constructor(data: Int32Array)
    constructor(data: Float32Array)
    constructor(data: Float64Array)
    open fun readHeader(): Boolean
    open fun readValue(): Any
    open fun transferArrayBuffer(id: Number, arrayBuffer: ArrayBuffer)
    open fun getWireFormatVersion(): Number
    open fun readUint32(): Number
    open fun readUint64(): dynamic /* JsTuple<Number, Number> */
    open fun readDouble(): Number
    open fun readRawBytes(length: Number): Buffer
}

external open class DefaultDeserializer : Deserializer

external fun serialize(value: Any): Buffer

external fun deserialize(data: Uint8Array): Any

external fun deserialize(data: Uint8ClampedArray): Any

external fun deserialize(data: Uint16Array): Any

external fun deserialize(data: Uint32Array): Any

external fun deserialize(data: Int8Array): Any

external fun deserialize(data: Int16Array): Any

external fun deserialize(data: Int32Array): Any

external fun deserialize(data: Float32Array): Any

external fun deserialize(data: Float64Array): Any