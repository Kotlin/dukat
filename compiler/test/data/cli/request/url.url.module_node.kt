@file:JsModule("url")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package url

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

external interface UrlObjectCommon {
    var auth: String?
        get() = definedExternally
        set(value) = definedExternally
    var hash: String?
        get() = definedExternally
        set(value) = definedExternally
    var host: String?
        get() = definedExternally
        set(value) = definedExternally
    var hostname: String?
        get() = definedExternally
        set(value) = definedExternally
    var href: String?
        get() = definedExternally
        set(value) = definedExternally
    var path: String?
        get() = definedExternally
        set(value) = definedExternally
    var pathname: String?
        get() = definedExternally
        set(value) = definedExternally
    var protocol: String?
        get() = definedExternally
        set(value) = definedExternally
    var search: String?
        get() = definedExternally
        set(value) = definedExternally
    var slashes: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface UrlObject : UrlObjectCommon {
    var port: dynamic /* String | Number */
        get() = definedExternally
        set(value) = definedExternally
    var query: dynamic /* String | Nothing? | ParsedUrlQueryInput */
        get() = definedExternally
        set(value) = definedExternally
}

external interface Url : UrlObjectCommon {
    var port: String?
        get() = definedExternally
        set(value) = definedExternally
    var query: dynamic /* String | Nothing? | ParsedUrlQuery */
        get() = definedExternally
        set(value) = definedExternally
}

external interface UrlWithParsedQuery : Url {
    override var query: ParsedUrlQuery
}

external interface UrlWithStringQuery : Url {
    override var query: String?
}

external fun parse(urlStr: String): UrlWithStringQuery

external fun parse(urlStr: String, parseQueryString: String?, slashesDenoteHost: Boolean? = definedExternally /* null */): UrlWithStringQuery

external fun parse(urlStr: String, parseQueryString: String /* true */, slashesDenoteHost: Boolean? = definedExternally /* null */): UrlWithParsedQuery

external fun parse(urlStr: String, parseQueryString: Boolean, slashesDenoteHost: Boolean? = definedExternally /* null */): Url

external fun format(URL: URL, options: URLFormatOptions? = definedExternally /* null */): String

external fun format(urlObject: UrlObject): String

external fun format(urlObject: String): String

external fun resolve(from: String, to: String): String

external fun domainToASCII(domain: String): String

external fun domainToUnicode(domain: String): String

external fun fileURLToPath(url: String): String

external fun fileURLToPath(url: URL): String

external fun pathToFileURL(url: String): URL

external interface URLFormatOptions {
    var auth: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var fragment: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var search: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var unicode: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external open class URL {
    constructor(input: String, base: String?)
    constructor(input: String, base: URL?)
    open var hash: String
    open var host: String
    open var hostname: String
    open var href: String
    open var origin: String
    open var password: String
    open var pathname: String
    open var port: String
    open var protocol: String
    open var search: String
    open var searchParams: URLSearchParams
    open var username: String
    override fun toString(): String
    open fun toJSON(): String
}

external interface `T$0` {
    @nativeGetter
    operator fun get(key: String): dynamic /* String | Array<String> | Nothing? */
    @nativeSetter
    operator fun set(key: String, value: String)
    @nativeSetter
    operator fun set(key: String, value: Array<String>)
    @nativeSetter
    operator fun set(key: String, value: Nothing?)
}

external open class URLSearchParams : Iterable<dynamic /* JsTuple<String, String> */> {
    constructor(init: URLSearchParams?)
    constructor(init: String?)
    constructor(init: `T$0`?)
    constructor(init: Iterable<dynamic /* JsTuple<String, String> */>?)
    constructor(init: Array<dynamic /* JsTuple<String, String> */>?)
    open fun append(name: String, value: String)
    open fun delete(name: String)
    open fun entries(): IterableIterator<dynamic /* JsTuple<String, String> */>
    open fun forEach(callback: (value: String, name: String, searchParams: URLSearchParams /* this */) -> Unit)
    open fun get(name: String): String?
    open fun getAll(name: String): Array<String>
    open fun has(name: String): Boolean
    open fun keys(): IterableIterator<String>
    open fun set(name: String, value: String)
    open fun sort()
    override fun toString(): String
    open fun values(): IterableIterator<String>
}