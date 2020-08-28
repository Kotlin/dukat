// [test] optionalConflictingMethods.kt
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

external interface WritableStateOptions {
    val write: ((encoding: String /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */) -> Unit)?
        get() = definedExternally
}

external interface `L$1` {
    @nativeInvoke
    operator fun invoke(arg: String)
    @nativeInvoke
    operator fun invoke(arg: Number)
}

external interface RealConflict {
    val conflictMethod: `L$1`?
        get() = definedExternally
}