// [test] functionTypedIntersectionParameter.kt
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

external interface FooPart<T>

external interface `T$1`<T> {
    var foo: T
    var bar: Any
}

external interface `T$0`<T> {
    var foo: T
    var sup: Any
}

external open class FooTypedUnion {
    open fun <T> baz(p: `T$0`<T>)
    open fun <T> bar(p: `T$1`<T> /* `T$1`<T> & FooPart<T> */)
}