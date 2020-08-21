@file:JsModule("punycode")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package punycode

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

external fun decode(string: String): String

external fun encode(string: String): String

external fun toUnicode(domain: String): String

external fun toASCII(domain: String): String

external interface ucs2 {
    fun decode(string: String): Array<Number>
    fun encode(codePoints: Array<Number>): String

    companion object : ucs2 by definedExternally
}

external var version: String