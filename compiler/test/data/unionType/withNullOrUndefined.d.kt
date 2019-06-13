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

external var foo: String? = definedExternally
external var bar: String? = definedExternally
external fun bar(a: String?): Foo?
external fun baz(a: Foo?, b: Number? = definedExternally /* null */): Any?
external interface `T$0` {
    @nativeGetter
    operator fun get(key: String?): String?
    @nativeSetter
    operator fun set(key: String?, value: String?)
}
external open class Foo(a: String?) {
    open fun someMethod(): dynamic /* String | Number | Nothing? */ = definedExternally
    open var foo: Foo? = definedExternally
    open var optionalFoo: String? = definedExternally
    open var optionalFoo2: String? = definedExternally
    open var optionalFoo3: String? = definedExternally
    open var refs: `T$0` = definedExternally
}
external interface IBar {
    fun someMethod(): dynamic /* String | Number | Nothing? */
    var foo: Foo?
    var optionalFoo: String? get() = definedExternally; set(value) = definedExternally
    var optionalFoo2: String? get() = definedExternally; set(value) = definedExternally
    var optionalFoo3: String? get() = definedExternally; set(value) = definedExternally
}
