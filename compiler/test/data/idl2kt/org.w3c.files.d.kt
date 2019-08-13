package org.w3c.files

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.css.masking.*
import org.w3c.dom.*
import org.w3c.dom.clipboard.*
import org.w3c.dom.css.*
import org.w3c.dom.events.*
import org.w3c.dom.mediacapture.*
import org.w3c.dom.parsing.*
import org.w3c.dom.pointerevents.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

/**
 * Exposes the JavaScript [Blob](https://developer.mozilla.org/en/docs/Web/API/Blob) to Kotlin
 */
external open class Blob(blobParts: Array<dynamic> = definedExternally, options: BlobPropertyBag = definedExternally) {
    open val size: Int
    open val type: String
    open val isClosed: Boolean
    fun slice(start: Int = definedExternally, end: Int = definedExternally, contentType: String = definedExternally): Blob
    fun close()
}

external interface BlobPropertyBag {
    var type: String? /* = "" */
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun BlobPropertyBag(type: String? = ""): BlobPropertyBag {
    val o = js("({})")
    o["type"] = type
    return o
}

/**
 * Exposes the JavaScript [File](https://developer.mozilla.org/en/docs/Web/API/File) to Kotlin
 */
external open class File(fileBits: Array<dynamic>, fileName: String, options: FilePropertyBag = definedExternally) : Blob {
    open val name: String
    open val lastModified: Int
}

external interface FilePropertyBag : BlobPropertyBag {
    var lastModified: Int?
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun FilePropertyBag(lastModified: Int? = undefined, type: String? = ""): FilePropertyBag {
    val o = js("({})")
    o["lastModified"] = lastModified
    o["type"] = type
    return o
}

/**
 * Exposes the JavaScript [FileList](https://developer.mozilla.org/en/docs/Web/API/FileList) to Kotlin
 */
external abstract class FileList : ItemArrayLike<File> {
    override val length: Int
    override fun item(index: Int): File?
}

@kotlin.internal.InlineOnly
inline operator fun FileList.get(index: Int): File? = asDynamic()[index]

/**
 * Exposes the JavaScript [FileReader](https://developer.mozilla.org/en/docs/Web/API/FileReader) to Kotlin
 */
external open class FileReader : EventTarget {
    open val readyState: Short
    open val result: dynamic
    open val error: dynamic
    var onloadstart: ((ProgressEvent) -> dynamic)?
    var onprogress: ((ProgressEvent) -> dynamic)?
    var onload: ((Event) -> dynamic)?
    var onabort: ((Event) -> dynamic)?
    var onerror: ((Event) -> dynamic)?
    var onloadend: ((Event) -> dynamic)?
    fun readAsArrayBuffer(blob: Blob)
    fun readAsBinaryString(blob: Blob)
    fun readAsText(blob: Blob, label: String = definedExternally)
    fun readAsDataURL(blob: Blob)
    fun abort()

    companion object {
        val EMPTY: Short
        val LOADING: Short
        val DONE: Short
    }
}

/**
 * Exposes the JavaScript [FileReaderSync](https://developer.mozilla.org/en/docs/Web/API/FileReaderSync) to Kotlin
 */
external open class FileReaderSync {
    fun readAsArrayBuffer(blob: Blob): ArrayBuffer
    fun readAsBinaryString(blob: Blob): String
    fun readAsText(blob: Blob, label: String = definedExternally): String
    fun readAsDataURL(blob: Blob): String
}

