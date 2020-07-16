// [test] simple.kt
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

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

open class Something {
    open var x: Number = 5
    open fun isChild(): Boolean = false
}

open class Child : Something {
    open var y: Number = 6
    override fun isChild(): Boolean = true
}

external fun isSomething(x: Any): Boolean

fun f() {
    var s: Any = Something()
    if (isSomething(s)) {
        s as Something
        console.log(s.x)
        if (s.isChild()) {
            s as Child
            console.log(s.y)
        }
    }
}