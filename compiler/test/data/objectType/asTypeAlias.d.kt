@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
package asTypeAlias

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

external interface I {
    fun foo(): String
}
external interface J {
    fun foo(): String
}
external interface K {
    fun bar(): Number
}
external interface Q
external interface W
external fun f(a: I, b: J, c: I, q: Q, w: W): K = definedExternally
external var x: I = definedExternally
external var y: I = definedExternally
external var z: J = definedExternally
external var q: Q = definedExternally
external var w: W = definedExternally
