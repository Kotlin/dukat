@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package myStream

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
import GlobalNamespace.WritableStream
import myStream.internal.Readable
import myStream.internal.Stream
import GlobalNamespace.ReadableStream

external interface `T$0` {
    var end: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

@JsModule("myStream")
external open class internal {
    open fun <T : WritableStream> pipe(destination: T, options: `T$0`? = definedExternally): T
    open class Stream : internal
    open class Readable : Stream, ReadableStream {
        override fun unpipe(destination: WritableStream?): Readable /* this */
    }
}

// ------------------------------------------------------------------------------------------
@file:JsQualifier("GlobalNamespace")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package GlobalNamespace

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
import myStream.`T$0`

external interface ReadableStream {
    fun <T : WritableStream> pipe(destination: T, options: `T$0`? = definedExternally): T
    fun unpipe(destination: WritableStream? = definedExternally): ReadableStream /* this */
}

external interface WritableStream