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

external var foo: dynamic /* String | Number */
external fun bar(a: String)
external fun bar(a: Number)
external fun bar(a: Foo)
external fun baz(a: String, b: Number)
external fun baz(a: String, b: Foo)
external fun baz(a: Number, b: Number)
external fun baz(a: Number, b: Foo)
external fun baz(a: Foo, b: Number)
external fun baz(a: Foo, b: Foo)
external interface `T$0` {
    @nativeGetter
    operator fun get(key: String): dynamic /* String | Number */
    @nativeSetter
    operator fun set(key: String, value: String)
    @nativeSetter
    operator fun set(key: String, value: Number)
}
external open class Foo {
    constructor(a: String)
    constructor(a: Number)
    open fun someMethod(): dynamic /* String | Number */
    open var foo: dynamic /* String | Number */
    open var optionalFoo: dynamic /* String | Number */
    open var refs: `T$0`
}
