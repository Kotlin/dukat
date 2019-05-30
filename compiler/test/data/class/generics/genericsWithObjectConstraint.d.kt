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

external interface `T$0` {
    var target: Any? get() = definedExternally; set(value) = definedExternally
}
external open class SomeClass {
    open fun <T : `T$0`> ping(source: T): Unit = definedExternally
    companion object {
        fun <T : `T$0`> foo(array: Array<T>, classes: Array<Any>? = definedExternally /* null */): Array<T> = definedExternally
    }
}
external interface `T$1` {
    var target: Any
}
external open class SomeOtherClass<T : `T$1`> {
    open fun ping(obj: T): Boolean = definedExternally
}
external interface OtherClassLikeInterface<T : `T$1`> {
    fun ping(obj: T): SomeOtherClass<T>
}
external interface `T$2` {
    var onTransformEnd: () -> Unit
}
external fun <T : `T$2`> transform(a: T): T = definedExternally