// [test] array.kt
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

open class A {
    open var a1 = arrayOf()
    open var a2 = arrayOf<String>()
    open fun f() {
        var a: Array<Number> = arrayOf()
        a = a + 1 + 2 + 3
        a = a + 4
        a = a + arrayOf(5, 6, 7)
        a.forEach({ x -> console.log(x) })
    }
}