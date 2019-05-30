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

external open class ExpectedOverrides {
    override fun equals(a: Any): Unit = definedExternally
    override fun hashCode(): Number = definedExternally
    override fun toString(): String = definedExternally
}
external open class ExpectedOverrides2 {
    override fun equals(a: Any): Unit = definedExternally
}
external open class ExpectedNoOverrides {
    open fun equals(): Unit = definedExternally
    open fun equals(a: Number): Unit = definedExternally
    open fun equals(a: String): Unit = definedExternally
    open fun hashCode(a: String): Number = definedExternally
    open fun toString(a: Number = definedExternally /* 1 */): Unit = definedExternally
}
