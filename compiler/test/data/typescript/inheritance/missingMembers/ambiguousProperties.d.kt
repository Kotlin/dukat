// [test] ambiguousProperties.kt
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

external interface InputOptions {
    var highWaterMark: Number?
        get() = definedExternally
        set(value) = definedExternally
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var objectMode: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface OutputOptions {
    var highWaterMark: Number?
        get() = definedExternally
        set(value) = definedExternally
    var decodeStrings: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var objectMode: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var defaultEncoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface DuplexOptions : InputOptions, OutputOptions {
    override var highWaterMark: Number?
        get() = definedExternally
        set(value) = definedExternally
    override var objectMode: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}