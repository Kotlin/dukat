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
    open var m1: Any = mutableMapOf()
    open var m2: Any = mutableMapOf<Any, Any>()
    open fun f() {
        var m: Any = mutableMapOf()
        m.set(3, 2)
        m.set(5, 1)
        console.log(m.get(3))
        for (_i: Any in m) {
            var x: Any = _i.key
            var y: Any = _i.value
            console.log("${x} ${y}")
        }
    }
}