// [test] overrideOptional.kt
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

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

external open class Stream

external open class Writable : Stream

external open class Readable : Stream

external open class Duplex : Readable

external interface ReadableOptions {
    val read: ((self: Readable, size: Number) -> Unit)?
        get() = definedExternally
}

external interface WriteableOptions {
    val write: ((self: Writable, chunk: Any, encoding: String, callback: (error: Error?) -> Unit) -> Unit)?
        get() = definedExternally
}

external interface DuplexOptions : ReadableOptions, WriteableOptions