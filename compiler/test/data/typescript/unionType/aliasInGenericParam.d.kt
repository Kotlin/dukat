// [test] aliasInGenericParam.kt
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

external interface OurObject {
    @nativeGetter
    operator fun get(x: String): dynamic /* String? | Number? | Boolean? | Date? | OurObject? | OurListArray? */
    @nativeSetter
    operator fun set(x: String, value: String)
    @nativeSetter
    operator fun set(x: String, value: Number)
    @nativeSetter
    operator fun set(x: String, value: Boolean)
    @nativeSetter
    operator fun set(x: String, value: Date)
    @nativeSetter
    operator fun set(x: String, value: OurObject)
    @nativeSetter
    operator fun set(x: String, value: OurListArray)
}

external interface OurListArray : ArrayList<dynamic /* String | Number | Boolean | Date | OurObject | OurListArray */>