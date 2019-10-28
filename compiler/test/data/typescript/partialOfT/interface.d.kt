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

external interface Low {
    var propInLow: String
    fun methodInLow(): Boolean
    fun lambdaInLow(): Boolean
}

external interface Some : Low {
    var propInSome: String
}

external interface LowPartial {
    var propInLow: String?
        get() = definedExternally
        set(value) = definedExternally
    var methodInLow: (() -> Boolean)?
        get() = definedExternally
        set(value) = definedExternally
    var lambdaInLow: (() -> Boolean)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SomePartial : LowPartial {
    var propInSome: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun usePartial(some: Some, partial_some: SomePartial)