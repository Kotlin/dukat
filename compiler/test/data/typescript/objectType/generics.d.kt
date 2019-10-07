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

external interface `T$0`<B> {
    fun foo(a: Any): B
}

external interface `T$1`<T> {
    fun bar(a: T): Boolean
}

external interface FooBazWithTypes<T> {
    fun <B> returnsB(b: B): `T$0`<B>
    fun acceptsT(): `T$1`<T>
}

external interface `T$2`<T> {
    fun bar(a: Any): T
    var baz: Any?
        get() = definedExternally
        set(value) = definedExternally
    var boo: T?
        get() = definedExternally
        set(value) = definedExternally
    var show: (overrideChecks: Boolean) -> Unit
}

external fun <T> withGenericObjectTypeParam(opt: `T$2`<T>)

external interface `T$3`<T> {
    var a: T
}

external interface `T$4`<T, S> {
    fun bar(a: Any): T
    fun foo(t: `T$3`<T>)
    fun foo(t: String)
    var baz: Any?
        get() = definedExternally
        set(value) = definedExternally
    var boo: S?
        get() = definedExternally
        set(value) = definedExternally
    var show: (overrideChecks: Boolean) -> Unit
}

external fun <T, S> withDoublyGenericObjectTypeParam(opt: `T$4`<T, S>)

external interface `T$5`<S> {
    fun bar(a: Any): S
}

external fun <S> returnsGenericObjectType(): `T$5`<S>

external var Tokens: Array<Any>

external interface `T$6` {
    var ping: () -> Boolean
}

external var PingableTokens: Array<`T$6`>

external interface `T$7`<S, T> {
    var source: S
    var target: T
}

external interface `T$8`<S, T> {
    fun handler(source: S, target: T): Boolean
}

external open class SomeSource<P, S> {
    open fun ping(): P
    open fun <T> withTarget(): `T$7`<S, T>
    open fun <T> getTargetHandler(): `T$8`<S, T>
}
