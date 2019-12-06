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

external interface RS {
    fun unshift(chunk: String, encoding: String /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally)
    fun unshift(chunk: Uint8Array, encoding: String /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally)
    fun unshift(chunk: Array<Number>, encoding: String /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally)
    fun shouldNotBeCopied(a: String): Array<String>
    fun shouldNotBeCopied(a: Array<String>): Array<String>
}

external open class R : RS {
    open fun unshift(chunk: Any, encoding: String = definedExternally)
    override fun shouldNotBeCopied(a: String): Array<String>
    override fun shouldNotBeCopied(a: Array<String>): Array<String>
    open fun unshift(chunk: Any)
    override fun unshift(chunk: String, encoding: String /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */)
    override fun unshift(chunk: Uint8Array, encoding: String /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */)
    override fun unshift(chunk: Array<Number>, encoding: String /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */)
}

external open class X : R {
    override fun shouldNotBeCopied(a: String): Array<String>
    override fun shouldNotBeCopied(a: Array<String>): Array<String>
    override fun unshift(chunk: Any, encoding: String)
    override fun unshift(chunk: Any)
}