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

external object foo {
    @nativeGetter
    operator fun get(someKey: String): Number?
    @nativeSetter
    operator fun set(someKey: String, value: Number)
}

external interface `T$0` {
    @nativeGetter
    operator fun get(someValue: String): Number?
    @nativeSetter
    operator fun set(someValue: String, value: Number)
}

external interface `T$1` {
    @nativeGetter
    operator fun get(someName: String): `T$0`?
    @nativeSetter
    operator fun set(someName: String, value: `T$0`)
}

external open class Foo {
    @nativeGetter
    open operator fun get(n: Number): Bar?
    @nativeSetter
    open operator fun set(n: Number, value: Bar)
    @nativeGetter
    open operator fun get(s: String): dynamic /* String | Array<String> */
    @nativeSetter
    open operator fun set(s: String, value: String)
    @nativeSetter
    open operator fun set(s: String, value: Array<String>)
    open var props: `T$1`
}

external open class Bar
