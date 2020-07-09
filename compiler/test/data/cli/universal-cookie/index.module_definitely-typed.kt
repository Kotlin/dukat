@file:JsModule("definitely-typed")
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

external interface CookiesByName {
    @nativeGetter
    operator fun get(cookieName: String): String?
    @nativeSetter
    operator fun set(cookieName: String, value: String)
}

external interface GetOpts {
    var doNotParse: Boolean
}

external interface CookieOpts {
    var path: String?
        get() = definedExternally
        set(value) = definedExternally
    var expires: Date?
        get() = definedExternally
        set(value) = definedExternally
    var maxAge: Number?
        get() = definedExternally
        set(value) = definedExternally
    var domain: String?
        get() = definedExternally
        set(value) = definedExternally
    var secure: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var httpOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

@JsName("default")
external open class Cookies {
    constructor(cookieHeader: String?)
    constructor(cookieHeader: CookiesByName?)
    open fun get(name: String, options: GetOpts? = definedExternally /* null */): String
    open fun getAll(options: GetOpts? = definedExternally /* null */): CookiesByName
    open fun set(name: String, value: String, options: CookieOpts? = definedExternally /* null */)
    open fun set(name: String, value: Any?, options: CookieOpts? = definedExternally /* null */)
    open fun remove(name: String, options: CookieOpts? = definedExternally /* null */)
    open fun addChangeListener(callback: ChangeListenerCallback)
    open fun removeChangeListener(callback: ChangeListenerCallback)
}