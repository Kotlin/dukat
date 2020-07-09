@file:JsQualifier("util.inspect")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package util.inspect

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

external object colors {
    @nativeGetter
    operator fun get(color: String): dynamic /* dynamic | Nothing? */
    @nativeSetter
    operator fun set(color: String, value: dynamic /* dynamic | Nothing? */)
}

external object styles {
    @nativeGetter
    operator fun get(style: String): String?
    @nativeSetter
    operator fun set(style: String, value: String?)
}

external var defaultOptions: util.InspectOptions

external var replDefaults: util.InspectOptions