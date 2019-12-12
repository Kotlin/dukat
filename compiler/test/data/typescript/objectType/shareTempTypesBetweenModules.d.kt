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
import Foo.`T$0`
import Foo.`T$1`

external fun withObjectTypeParam(opt: `T$0`)

external fun returnsObjectType(): `T$1`

external object foo {
    fun bar(a: Any): Number
    fun baz(a: Any, b: Any, c: String): Boolean
}

// ------------------------------------------------------------------------------------------
@file:JsQualifier("Foo")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package Foo

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
    fun bar(a: Any): Number
    var baz: Any?
        get() = definedExternally
        set(value) = definedExternally
    var boo: Any?
        get() = definedExternally
        set(value) = definedExternally
    var show: (overrideChecks: Boolean) -> Unit
}

external fun withObjectTypeParam(opt: `T$0`)

external interface `T$1` {
    var value: Any?
        get() = definedExternally
        set(value) = definedExternally
    var done: Boolean
}

external fun returnsObjectType(): `T$1`

external object foo {
    fun bar(a: Any): Number
    fun baz(a: Any, b: Any, c: String): Boolean
}

// ------------------------------------------------------------------------------------------
@file:JsQualifier("Bar")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package Bar

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
import Foo.`T$0`
import Foo.`T$1`

external fun someFunction(opt: `T$0`)

external fun anotherReturnsObjectType(): `T$1`

external object foo {
    fun bar(a: Any): Number
    fun baz(a: Any, b: Any, c: String): Boolean
}