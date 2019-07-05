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

external open class ExpectedOverrides {
    override fun equals(a: Any): Unit
    override fun hashCode(): Number
    override fun toString(): String
}
external open class ExpectedOverrides2 {
    override fun equals(a: Any): Unit
}
external open class ExpectedNoOverrides {
    open fun equals(): Unit
    open fun equals(a: Number): Unit
    open fun equals(a: String): Unit
    open fun hashCode(a: String): Number
    open fun toString(a: Number = definedExternally /* 1 */): Unit
}
