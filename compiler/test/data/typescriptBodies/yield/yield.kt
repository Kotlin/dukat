// [test] yield.kt
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

import kotlin.js.*
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
import tsstdlib.Iterable

open class Yielder {
    open fun f(): Iterable<Number> {
        return Iterable({
            iterator({
                var i = 1
                while (true) {
                    yield(i)
                    i++
                }
            })
        })
    }
}

fun g() {
    var yielder = Yielder()
    for (x in yielder.f()) {
        if (x > 10) {
            break
        }
        console.log(x)
    }
}