@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

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
    var x: Number
    var y: Number
    var z: Number
}

external interface `T$1` {
    var x: Number
    var y: Number
    var z: Number
    var negative: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external fun generateVector(vectorProvider: (`0`: Any? /* = null */, `1`: Any? /* = null */, `2`: Any? /* = null */) -> `T$0`): `T$1`

external fun generateVector(vectorProvider: (`0`: Any? /* = null */, `1`: Any? /* = null */, `2`: Any? /* = null */) -> Any?): `T$1`