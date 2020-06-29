// [test] set.kt
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
    open var s1: Any = mutableSetOf()
    open var s2: Any = mutableSetOf<Any>()
    open fun f() {
        var s: Set<Number> = mutableSetOf()
        s.add(3)
        s.add(5)
        console.log(s.contains(3))
        s.forEach({ x: Any -> console.log(x) })
        s.remove(3)
        console.log(s.contains(3))
    }
}