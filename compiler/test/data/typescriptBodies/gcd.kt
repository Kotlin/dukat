// [test] gcd.kt
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

fun gcd(a: Number, b: Number): Number {
    a = Math.abs(a)
    b = Math.abs(b)
    if (b > a) {
        var temp: Number = a
        a = b
        b = temp
    }
    while (true) {
        a %= b
        if (a == 0) {
            return b
        }
        b %= a
        if (b == 0) {
            return a
        }
    }
}