@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")

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

external interface AnimatedValue
external interface Rec {
    var foo: Rec
}
external interface `T$0` {
    @nativeGetter
    operator fun get(key: String): dynamic /* `T$0` | AnimatedValue */
    @nativeSetter
    operator fun set(key: String, value: `T$0`)
    @nativeSetter
    operator fun set(key: String, value: AnimatedValue)
}
external fun foo(): dynamic /* `T$0` | AnimatedValue */ = definedExternally
external fun bar(d: `T$0`): Unit = definedExternally
external fun bar(d: AnimatedValue): Unit = definedExternally
external fun boo(): Rec = definedExternally
external fun baz(d: Rec): Unit = definedExternally
