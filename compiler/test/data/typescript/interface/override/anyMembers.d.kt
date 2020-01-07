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

external interface ExpectedOverrides {
    override fun equals(a: Any?)
    override fun hashCode(): Number
    override fun toString(): String
}

external interface ExpectedOverrides2 {
    override fun equals(a: Any?)
}

external interface ExpectedNoOverrides {
    fun equals()
    fun equals(a: Any)
    fun equals(a: Number)
    fun equals(a: String)
    fun hashCode(a: String): Number
    fun toString(a: Number = definedExternally)
}
