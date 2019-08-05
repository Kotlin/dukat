package org.w3c.dom.url

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.css.masking.*
import org.w3c.dom.*
import org.w3c.dom.clipboard.*
import org.w3c.dom.css.*
import org.w3c.dom.events.*
import org.w3c.dom.mediacapture.*
import org.w3c.dom.parsing.*
import org.w3c.dom.pointerevents.*
import org.w3c.dom.svg.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external open class URL(url: String, base: String = definedExternally) {
    var href: String
    open val origin: String
    var protocol: String
    var username: String
    var password: String
    var host: String
    var hostname: String
    var port: String
    var pathname: String
    var search: String
    open val searchParams: URLSearchParams
    var hash: String

    companion object {
        fun domainToASCII(domain: String): String
        fun domainToUnicode(domain: String): String
        fun createObjectURL(blob: Blob): String
        fun createFor(blob: Blob): String
        fun revokeObjectURL(url: String)
    }
}

external open class URLSearchParams(init: dynamic = definedExternally) {
    fun append(name: String, value: String)
    fun delete(name: String)
    fun get(name: String): String?
    fun getAll(name: String): Array<String>
    fun has(name: String): Boolean
    fun set(name: String, value: String)
}

