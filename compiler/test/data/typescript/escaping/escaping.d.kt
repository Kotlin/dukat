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

external var `val`: Any

external var `$foo`: Boolean

external fun `bar$`(`ba$z`: Number)

external fun `fun`()

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface This {
    operator fun get(key: String): Any?
    operator fun set(key: String, value: Any?)
    var this_one_shouldnt_be_escaped: Boolean
    var `when`: String
    var `typealias`: Number
    var `typeof`: Number
    var `this`: This
    fun `in`(obj: Foo)
    fun `is`(value: Any): Boolean
    fun `return`(): Any
    fun `return`(self: This): Number
    fun `throw`(reason: Error)
    fun `try`(fn: () -> Any)

    companion object {
        var `$foo`: Boolean
        fun `bar$`(`ba$z`: Number)
        var aaa: `when`.`interface`
        var bbb: `when`.`$foo`
    }
}

external open class `is`<`interface`> {
    open var `as`: Number
    open fun `package`(a: Any): Boolean

    companion object {
        fun `class`(): This
        fun `in`(obj: Foo)
        fun `is`(value: Any): Boolean
        fun `return`(): Any
        fun `return`(self: This): Number
        fun `throw`(reason: Error)
        fun `try`(fn: () -> Any)
    }
}

external fun <T, U> When(value: `when`.Promise<T>, transform: (param_val: T) -> U): `fun`.Promise<U>

external var `_`: `__`.`___`

typealias WatchHandler<T> = (param_val: T, oldVal: T) -> Unit

external open class `$tring`

external open class Foo

// ------------------------------------------------------------------------------------------
@file:JsQualifier("when")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package `when`

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
import `$boo`.`typealias`
import `$tring`

external var `$`: Boolean

external fun `package`(param_as: bar.string.`interface`, b: `typealias`): `$tring`

external interface `interface`

external interface `$foo`

external interface Promise<T>

// ------------------------------------------------------------------------------------------
@file:JsQualifier("bar.string")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package bar.string

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

external interface `interface`

// ------------------------------------------------------------------------------------------
@file:JsQualifier("$boo")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package `$boo`

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

external interface `typealias`

// ------------------------------------------------------------------------------------------
@file:JsQualifier("__")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package `__`

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

external interface `___`

external interface _OK_

// ------------------------------------------------------------------------------------------
@file:JsQualifier("__.xxx")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package `__`.xxx

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

external interface Amsterdam

// ------------------------------------------------------------------------------------------
@file:JsQualifier("fun")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package `fun`

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

external interface Promise<T>