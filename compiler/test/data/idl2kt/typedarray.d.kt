package org.khronos.webgl

import kotlin.js.*

/**
 * Exposes the JavaScript [ArrayBuffer](https://developer.mozilla.org/en/docs/Web/API/ArrayBuffer) to Kotlin
 */
public external open class ArrayBuffer(length: Int) : BufferDataSource {
    open val byteLength: Int
    fun slice(begin: Int, end: Int = definedExternally): ArrayBuffer

    companion object {
        fun isView(value: Any?): Boolean
    }
}

/**
 * Exposes the JavaScript [ArrayBufferView](https://developer.mozilla.org/en/docs/Web/API/ArrayBufferView) to Kotlin
 */
public external interface ArrayBufferView : BufferDataSource {
    val buffer: ArrayBuffer
    val byteOffset: Int
    val byteLength: Int
}

/**
 * Exposes the JavaScript [Int8Array](https://developer.mozilla.org/en/docs/Web/API/Int8Array) to Kotlin
 */
public external open class Int8Array : ArrayBufferView {
    constructor(length: Int)
    constructor(array: Int8Array)
    constructor(array: Array<Byte>)
    constructor(buffer: ArrayBuffer, byteOffset: Int = definedExternally, length: Int = definedExternally)
    open val length: Int
    override val buffer: ArrayBuffer
    override val byteOffset: Int
    override val byteLength: Int
    fun set(array: Int8Array, offset: Int = definedExternally)
    fun set(array: Array<Byte>, offset: Int = definedExternally)
    fun subarray(start: Int, end: Int): Int8Array

    companion object {
        val BYTES_PER_ELEMENT: Int
    }
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Int8Array.get(index: Int): Byte = asDynamic()[index]

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Int8Array.set(index: Int, value: Byte) { asDynamic()[index] = value }

/**
 * Exposes the JavaScript [Uint8Array](https://developer.mozilla.org/en/docs/Web/API/Uint8Array) to Kotlin
 */
public external open class Uint8Array : ArrayBufferView {
    constructor(length: Int)
    constructor(array: Uint8Array)
    constructor(array: Array<Byte>)
    constructor(buffer: ArrayBuffer, byteOffset: Int = definedExternally, length: Int = definedExternally)
    open val length: Int
    override val buffer: ArrayBuffer
    override val byteOffset: Int
    override val byteLength: Int
    fun set(array: Uint8Array, offset: Int = definedExternally)
    fun set(array: Array<Byte>, offset: Int = definedExternally)
    fun subarray(start: Int, end: Int): Uint8Array

    companion object {
        val BYTES_PER_ELEMENT: Int
    }
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Uint8Array.get(index: Int): Byte = asDynamic()[index]

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Uint8Array.set(index: Int, value: Byte) { asDynamic()[index] = value }

/**
 * Exposes the JavaScript [Uint8ClampedArray](https://developer.mozilla.org/en/docs/Web/API/Uint8ClampedArray) to Kotlin
 */
public external open class Uint8ClampedArray : ArrayBufferView {
    constructor(length: Int)
    constructor(array: Uint8ClampedArray)
    constructor(array: Array<Byte>)
    constructor(buffer: ArrayBuffer, byteOffset: Int = definedExternally, length: Int = definedExternally)
    open val length: Int
    override val buffer: ArrayBuffer
    override val byteOffset: Int
    override val byteLength: Int
    fun set(array: Uint8ClampedArray, offset: Int = definedExternally)
    fun set(array: Array<Byte>, offset: Int = definedExternally)
    fun subarray(start: Int, end: Int): Uint8ClampedArray

    companion object {
        val BYTES_PER_ELEMENT: Int
    }
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Uint8ClampedArray.get(index: Int): Byte = asDynamic()[index]

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Uint8ClampedArray.set(index: Int, value: Byte) { asDynamic()[index] = value }

/**
 * Exposes the JavaScript [Int16Array](https://developer.mozilla.org/en/docs/Web/API/Int16Array) to Kotlin
 */
public external open class Int16Array : ArrayBufferView {
    constructor(length: Int)
    constructor(array: Int16Array)
    constructor(array: Array<Short>)
    constructor(buffer: ArrayBuffer, byteOffset: Int = definedExternally, length: Int = definedExternally)
    open val length: Int
    override val buffer: ArrayBuffer
    override val byteOffset: Int
    override val byteLength: Int
    fun set(array: Int16Array, offset: Int = definedExternally)
    fun set(array: Array<Short>, offset: Int = definedExternally)
    fun subarray(start: Int, end: Int): Int16Array

    companion object {
        val BYTES_PER_ELEMENT: Int
    }
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Int16Array.get(index: Int): Short = asDynamic()[index]

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Int16Array.set(index: Int, value: Short) { asDynamic()[index] = value }

/**
 * Exposes the JavaScript [Uint16Array](https://developer.mozilla.org/en/docs/Web/API/Uint16Array) to Kotlin
 */
public external open class Uint16Array : ArrayBufferView {
    constructor(length: Int)
    constructor(array: Uint16Array)
    constructor(array: Array<Short>)
    constructor(buffer: ArrayBuffer, byteOffset: Int = definedExternally, length: Int = definedExternally)
    open val length: Int
    override val buffer: ArrayBuffer
    override val byteOffset: Int
    override val byteLength: Int
    fun set(array: Uint16Array, offset: Int = definedExternally)
    fun set(array: Array<Short>, offset: Int = definedExternally)
    fun subarray(start: Int, end: Int): Uint16Array

    companion object {
        val BYTES_PER_ELEMENT: Int
    }
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Uint16Array.get(index: Int): Short = asDynamic()[index]

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Uint16Array.set(index: Int, value: Short) { asDynamic()[index] = value }

/**
 * Exposes the JavaScript [Int32Array](https://developer.mozilla.org/en/docs/Web/API/Int32Array) to Kotlin
 */
public external open class Int32Array : ArrayBufferView {
    constructor(length: Int)
    constructor(array: Int32Array)
    constructor(array: Array<Int>)
    constructor(buffer: ArrayBuffer, byteOffset: Int = definedExternally, length: Int = definedExternally)
    open val length: Int
    override val buffer: ArrayBuffer
    override val byteOffset: Int
    override val byteLength: Int
    fun set(array: Int32Array, offset: Int = definedExternally)
    fun set(array: Array<Int>, offset: Int = definedExternally)
    fun subarray(start: Int, end: Int): Int32Array

    companion object {
        val BYTES_PER_ELEMENT: Int
    }
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Int32Array.get(index: Int): Int = asDynamic()[index]

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Int32Array.set(index: Int, value: Int) { asDynamic()[index] = value }

/**
 * Exposes the JavaScript [Uint32Array](https://developer.mozilla.org/en/docs/Web/API/Uint32Array) to Kotlin
 */
public external open class Uint32Array : ArrayBufferView {
    constructor(length: Int)
    constructor(array: Uint32Array)
    constructor(array: Array<Int>)
    constructor(buffer: ArrayBuffer, byteOffset: Int = definedExternally, length: Int = definedExternally)
    open val length: Int
    override val buffer: ArrayBuffer
    override val byteOffset: Int
    override val byteLength: Int
    fun set(array: Uint32Array, offset: Int = definedExternally)
    fun set(array: Array<Int>, offset: Int = definedExternally)
    fun subarray(start: Int, end: Int): Uint32Array

    companion object {
        val BYTES_PER_ELEMENT: Int
    }
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Uint32Array.get(index: Int): Int = asDynamic()[index]

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Uint32Array.set(index: Int, value: Int) { asDynamic()[index] = value }

/**
 * Exposes the JavaScript [Float32Array](https://developer.mozilla.org/en/docs/Web/API/Float32Array) to Kotlin
 */
public external open class Float32Array : ArrayBufferView {
    constructor(length: Int)
    constructor(array: Float32Array)
    constructor(array: Array<Float>)
    constructor(buffer: ArrayBuffer, byteOffset: Int = definedExternally, length: Int = definedExternally)
    open val length: Int
    override val buffer: ArrayBuffer
    override val byteOffset: Int
    override val byteLength: Int
    fun set(array: Float32Array, offset: Int = definedExternally)
    fun set(array: Array<Float>, offset: Int = definedExternally)
    fun subarray(start: Int, end: Int): Float32Array

    companion object {
        val BYTES_PER_ELEMENT: Int
    }
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Float32Array.get(index: Int): Float = asDynamic()[index]

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Float32Array.set(index: Int, value: Float) { asDynamic()[index] = value }

/**
 * Exposes the JavaScript [Float64Array](https://developer.mozilla.org/en/docs/Web/API/Float64Array) to Kotlin
 */
public external open class Float64Array : ArrayBufferView {
    constructor(length: Int)
    constructor(array: Float64Array)
    constructor(array: Array<Double>)
    constructor(buffer: ArrayBuffer, byteOffset: Int = definedExternally, length: Int = definedExternally)
    open val length: Int
    override val buffer: ArrayBuffer
    override val byteOffset: Int
    override val byteLength: Int
    fun set(array: Float64Array, offset: Int = definedExternally)
    fun set(array: Array<Double>, offset: Int = definedExternally)
    fun subarray(start: Int, end: Int): Float64Array

    companion object {
        val BYTES_PER_ELEMENT: Int
    }
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Float64Array.get(index: Int): Double = asDynamic()[index]

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Float64Array.set(index: Int, value: Double) { asDynamic()[index] = value }

/**
 * Exposes the JavaScript [DataView](https://developer.mozilla.org/en/docs/Web/API/DataView) to Kotlin
 */
public external open class DataView(buffer: ArrayBuffer, byteOffset: Int = definedExternally, byteLength: Int = definedExternally) : ArrayBufferView {
    override val buffer: ArrayBuffer
    override val byteOffset: Int
    override val byteLength: Int
    fun getInt8(byteOffset: Int): Byte
    fun getUint8(byteOffset: Int): Byte
    fun getInt16(byteOffset: Int, littleEndian: Boolean = definedExternally): Short
    fun getUint16(byteOffset: Int, littleEndian: Boolean = definedExternally): Short
    fun getInt32(byteOffset: Int, littleEndian: Boolean = definedExternally): Int
    fun getUint32(byteOffset: Int, littleEndian: Boolean = definedExternally): Int
    fun getFloat32(byteOffset: Int, littleEndian: Boolean = definedExternally): Float
    fun getFloat64(byteOffset: Int, littleEndian: Boolean = definedExternally): Double
    fun setInt8(byteOffset: Int, value: Byte)
    fun setUint8(byteOffset: Int, value: Byte)
    fun setInt16(byteOffset: Int, value: Short, littleEndian: Boolean = definedExternally)
    fun setUint16(byteOffset: Int, value: Short, littleEndian: Boolean = definedExternally)
    fun setInt32(byteOffset: Int, value: Int, littleEndian: Boolean = definedExternally)
    fun setUint32(byteOffset: Int, value: Int, littleEndian: Boolean = definedExternally)
    fun setFloat32(byteOffset: Int, value: Float, littleEndian: Boolean = definedExternally)
    fun setFloat64(byteOffset: Int, value: Double, littleEndian: Boolean = definedExternally)
}
