@file:JsModule("request")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package request

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

external interface RequestAPI<TRequest : Request, TOptions : CoreOptions, TUriUrlOptions> {
    fun defaults(options: TOptions): RequestAPI<TRequest, TOptions, dynamic /* UriOptions | UrlOptions */>
    fun defaults(options: UriOptions): DefaultUriUrlRequestApi<TRequest, TOptions, dynamic /* UriOptions | UrlOptions | Any */>
    fun defaults(options: UrlOptions): DefaultUriUrlRequestApi<TRequest, TOptions, dynamic /* UriOptions | UrlOptions | Any */>
    @nativeInvoke
    operator fun invoke(uri: String, options: TOptions? = definedExternally /* null */, callback: RequestCallback? = definedExternally /* null */): TRequest
    @nativeInvoke
    operator fun invoke(uri: String, callback: RequestCallback? = definedExternally /* null */): TRequest
    @nativeInvoke
    operator fun invoke(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun get(uri: String, options: TOptions? = definedExternally /* null */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun get(uri: String, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun get(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun post(uri: String, options: TOptions? = definedExternally /* null */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun post(uri: String, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun post(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun put(uri: String, options: TOptions? = definedExternally /* null */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun put(uri: String, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun put(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun head(uri: String, options: TOptions? = definedExternally /* null */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun head(uri: String, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun head(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun patch(uri: String, options: TOptions? = definedExternally /* null */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun patch(uri: String, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun patch(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun del(uri: String, options: TOptions? = definedExternally /* null */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun del(uri: String, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun del(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun delete(uri: String, options: TOptions? = definedExternally /* null */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun delete(uri: String, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun delete(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback? = definedExternally /* null */): TRequest
    fun initParams(uri: String, options: TOptions? = definedExternally /* null */, callback: RequestCallback? = definedExternally /* null */): dynamic /* UriOptions | UrlOptions */
    fun initParams(uriOrOpts: String, callback: RequestCallback? = definedExternally /* null */): dynamic /* UriOptions | UrlOptions */
    fun initParams(uriOrOpts: UriOptions, callback: RequestCallback? = definedExternally /* null */): dynamic /* UriOptions | UrlOptions */
    fun initParams(uriOrOpts: UrlOptions, callback: RequestCallback? = definedExternally /* null */): dynamic /* UriOptions | UrlOptions */
    fun forever(agentOptions: Any, optionsArg: Any): TRequest
    fun jar(store: Any? = definedExternally /* null */): CookieJar
    fun cookie(str: String): Cookie?
    var debug: Boolean
}

external interface DefaultUriUrlRequestApi<TRequest : Request, TOptions : CoreOptions, TUriUrlOptions> : RequestAPI<TRequest, TOptions, TUriUrlOptions> {
    override fun defaults(options: TOptions): DefaultUriUrlRequestApi<TRequest, TOptions, dynamic /* UriOptions | UrlOptions | Any */>
    @nativeInvoke
    operator fun invoke(callback: RequestCallback? = definedExternally /* null */): TRequest
    override fun get(uri: String, options: TOptions?, callback: RequestCallback?): TRequest
    override fun get(uri: String, callback: RequestCallback?): TRequest
    override fun get(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback?): TRequest
    fun get(callback: RequestCallback? = definedExternally /* null */): TRequest
    override fun post(uri: String, options: TOptions?, callback: RequestCallback?): TRequest
    override fun post(uri: String, callback: RequestCallback?): TRequest
    override fun post(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback?): TRequest
    fun post(callback: RequestCallback? = definedExternally /* null */): TRequest
    override fun put(uri: String, options: TOptions?, callback: RequestCallback?): TRequest
    override fun put(uri: String, callback: RequestCallback?): TRequest
    override fun put(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback?): TRequest
    fun put(callback: RequestCallback? = definedExternally /* null */): TRequest
    override fun head(uri: String, options: TOptions?, callback: RequestCallback?): TRequest
    override fun head(uri: String, callback: RequestCallback?): TRequest
    override fun head(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback?): TRequest
    fun head(callback: RequestCallback? = definedExternally /* null */): TRequest
    override fun patch(uri: String, options: TOptions?, callback: RequestCallback?): TRequest
    override fun patch(uri: String, callback: RequestCallback?): TRequest
    override fun patch(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback?): TRequest
    fun patch(callback: RequestCallback? = definedExternally /* null */): TRequest
    override fun del(uri: String, options: TOptions?, callback: RequestCallback?): TRequest
    override fun del(uri: String, callback: RequestCallback?): TRequest
    override fun del(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback?): TRequest
    fun del(callback: RequestCallback? = definedExternally /* null */): TRequest
    override fun delete(uri: String, options: TOptions?, callback: RequestCallback?): TRequest
    override fun delete(uri: String, callback: RequestCallback?): TRequest
    override fun delete(options: TUriUrlOptions /* TUriUrlOptions & TOptions */, callback: RequestCallback?): TRequest
    fun delete(callback: RequestCallback? = definedExternally /* null */): TRequest
}

external interface CoreOptions {
    var baseUrl: String?
        get() = definedExternally
        set(value) = definedExternally
    var callback: RequestCallback?
        get() = definedExternally
        set(value) = definedExternally
    var jar: dynamic /* CookieJar | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var formData: Json?
        get() = definedExternally
        set(value) = definedExternally
    var form: dynamic /* Json | String */
        get() = definedExternally
        set(value) = definedExternally
    var auth: AuthOptions?
        get() = definedExternally
        set(value) = definedExternally
    var oauth: OAuthOptions?
        get() = definedExternally
        set(value) = definedExternally
    var aws: AWSOptions?
        get() = definedExternally
        set(value) = definedExternally
    var hawk: HawkOptions?
        get() = definedExternally
        set(value) = definedExternally
    var qs: Any?
        get() = definedExternally
        set(value) = definedExternally
    var qsStringifyOptions: Any?
        get() = definedExternally
        set(value) = definedExternally
    var qsParseOptions: Any?
        get() = definedExternally
        set(value) = definedExternally
    var json: Any?
        get() = definedExternally
        set(value) = definedExternally
    var jsonReviver: ((key: String, value: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var jsonReplacer: ((key: String, value: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var multipart: dynamic /* Array<RequestPart> | Multipart */
        get() = definedExternally
        set(value) = definedExternally
    var agent: dynamic /* http.Agent | https.Agent */
        get() = definedExternally
        set(value) = definedExternally
    var agentOptions: dynamic /* http.AgentOptions | https.AgentOptions */
        get() = definedExternally
        set(value) = definedExternally
    var agentClass: Any?
        get() = definedExternally
        set(value) = definedExternally
    var forever: Any?
        get() = definedExternally
        set(value) = definedExternally
    var host: String?
        get() = definedExternally
        set(value) = definedExternally
    var port: Number?
        get() = definedExternally
        set(value) = definedExternally
    var method: String?
        get() = definedExternally
        set(value) = definedExternally
    var headers: Headers?
        get() = definedExternally
        set(value) = definedExternally
    var body: Any?
        get() = definedExternally
        set(value) = definedExternally
    var family: dynamic /* 4 | 6 */
        get() = definedExternally
        set(value) = definedExternally
    var followRedirect: dynamic /* Boolean | (response: http.IncomingMessage) -> Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var followAllRedirects: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var followOriginalHttpMethod: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var maxRedirects: Number?
        get() = definedExternally
        set(value) = definedExternally
    var removeRefererHeader: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var pool: Any?
        get() = definedExternally
        set(value) = definedExternally
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
    var localAddress: String?
        get() = definedExternally
        set(value) = definedExternally
    var proxy: Any?
        get() = definedExternally
        set(value) = definedExternally
    var tunnel: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var strictSSL: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var rejectUnauthorized: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var time: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var gzip: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var preambleCRLF: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var postambleCRLF: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var withCredentials: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var key: Buffer?
        get() = definedExternally
        set(value) = definedExternally
    var cert: Buffer?
        get() = definedExternally
        set(value) = definedExternally
    var passphrase: String?
        get() = definedExternally
        set(value) = definedExternally
    var ca: dynamic /* String | Buffer | Array<String> | Array<Buffer> */
        get() = definedExternally
        set(value) = definedExternally
    var har: HttpArchiveRequest?
        get() = definedExternally
        set(value) = definedExternally
    var useQuerystring: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface UriOptions {
    var uri: dynamic /* String | Url */
}

external interface UrlOptions {
    var url: dynamic /* String | Url */
}

external interface `T$0` {
    var mimeType: String?
        get() = definedExternally
        set(value) = definedExternally
    var params: Array<NameValuePair>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface HttpArchiveRequest {
    var url: String?
        get() = definedExternally
        set(value) = definedExternally
    var method: String?
        get() = definedExternally
        set(value) = definedExternally
    var headers: Array<NameValuePair>?
        get() = definedExternally
        set(value) = definedExternally
    var postData: `T$0`?
        get() = definedExternally
        set(value) = definedExternally
}

external interface NameValuePair {
    var name: String
    var value: String
}

external interface `T$1` {
    var `content-type`: String?
        get() = definedExternally
        set(value) = definedExternally
    var body: String
}

external interface Multipart {
    var chunked: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var data: Array<`T$1`>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RequestPart {
    var headers: Headers?
        get() = definedExternally
        set(value) = definedExternally
    var body: Any
}

external interface `T$2` {
    @nativeGetter
    operator fun get(key: String): dynamic /* http.Agent | https.Agent */
    @nativeSetter
    operator fun set(key: String, value: http.Agent)
    @nativeSetter
    operator fun set(key: String, value: https.Agent)
}

external interface `T$3` {
    var href: String
    var pathname: String
}

external interface `T$4` {
    var socket: Number
    var lookup: Number
    var connect: Number
    var response: Number
    var end: Number
}

external interface Request : caseless.Httpified, stream.Stream {
    var readable: Boolean
    var writable: Boolean
    var explicitMethod: String /* true */
    fun debug(vararg args: Any)
    fun pipeDest(dest: Any)
    fun qs(q: Any?, clobber: Boolean? = definedExternally /* null */): Request
    fun form(): FormData
    fun form(form: Any): Request
    fun multipart(multipart: Array<RequestPart>): Request
    fun json(`val`: Any): Request
    fun aws(opts: AWSOptions, now: Boolean? = definedExternally /* null */): Request
    fun hawk(opts: HawkOptions)
    fun auth(username: String, password: String, sendImmediately: Boolean? = definedExternally /* null */, bearer: String? = definedExternally /* null */): Request
    fun oauth(oauth: OAuthOptions): Request
    fun jar(jar: CookieJar): Request
    fun on(event: String, listener: (args: Array<Any>) -> Unit): Request /* this */
    fun on(event: String /* 'request' */, listener: (req: http.ClientRequest) -> Unit): Request /* this */
    fun on(event: String /* 'response' */, listener: (resp: Response) -> Unit): Request /* this */
    fun on(event: String /* 'data' */, listener: (data: dynamic /* Buffer | String */) -> Unit): Request /* this */
    fun on(event: String /* 'error' */, listener: (e: Error) -> Unit): Request /* this */
    fun on(event: String /* 'complete' */, listener: (resp: Response, body: dynamic /* String | Buffer */) -> Unit): Request /* this */
    fun on(event: String /* 'pipe' */, listener: (src: stream.Readable) -> Unit): Request /* this */
    fun on(event: String /* 'socket' */, listener: (src: net.Socket) -> Unit): Request /* this */
    fun write(buffer: Buffer, cb: ((err: Error? /* = null */) -> Unit)? = definedExternally /* null */): Boolean
    fun write(buffer: String, cb: ((err: Error? /* = null */) -> Unit)? = definedExternally /* null */): Boolean
    fun write(str: String, encoding: String? = definedExternally /* null */, cb: ((err: Error? /* = null */) -> Unit)? = definedExternally /* null */): Boolean
    fun end(cb: (() -> Unit)? = definedExternally /* null */)
    fun end(chunk: String, cb: (() -> Unit)? = definedExternally /* null */)
    fun end(chunk: Buffer, cb: (() -> Unit)? = definedExternally /* null */)
    fun end(str: String, encoding: String? = definedExternally /* null */, cb: (() -> Unit)? = definedExternally /* null */)
    fun pause()
    fun resume()
    fun abort()
    fun destroy()
    fun toJSON(): RequestAsJSON
    var host: String?
        get() = definedExternally
        set(value) = definedExternally
    var port: Number?
        get() = definedExternally
        set(value) = definedExternally
    var followAllRedirects: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var followOriginalHttpMethod: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var maxRedirects: Number?
        get() = definedExternally
        set(value) = definedExternally
    var removeRefererHeader: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
    var localAddress: String?
        get() = definedExternally
        set(value) = definedExternally
    var strictSSL: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var rejectUnauthorized: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var time: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var gzip: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var preambleCRLF: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var postambleCRLF: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var withCredentials: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var key: Buffer?
        get() = definedExternally
        set(value) = definedExternally
    var cert: Buffer?
        get() = definedExternally
        set(value) = definedExternally
    var passphrase: String?
        get() = definedExternally
        set(value) = definedExternally
    var ca: dynamic /* String | Buffer | Array<String> | Array<Buffer> */
        get() = definedExternally
        set(value) = definedExternally
    var har: HttpArchiveRequest?
        get() = definedExternally
        set(value) = definedExternally
    var headers: Headers
    var method: String
    var pool: dynamic /* false | `T$2` */
    var dests: Array<stream.Readable>
    var callback: RequestCallback?
        get() = definedExternally
        set(value) = definedExternally
    var uri: Url /* Url & `T$3` */
    var proxy: dynamic /* Nothing? | String | Url */
    var tunnel: Boolean
    var setHost: Boolean
    var path: String
    var agent: dynamic /* false | http.Agent | https.Agent */
    var body: dynamic /* Buffer | Array<Buffer> | String | Array<String> | stream.Readable */
    var timing: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var src: stream.Readable?
        get() = definedExternally
        set(value) = definedExternally
    var href: String
    var startTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var startTimeNow: Number?
        get() = definedExternally
        set(value) = definedExternally
    var timings: `T$4`?
        get() = definedExternally
        set(value) = definedExternally
    var elapsedTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var response: Response?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$5` {
    var wait: Number
    var dns: Number
    var tcp: Number
    var firstByte: Number
    var download: Number
    var total: Number
}

external interface Response : http.IncomingMessage {
    var statusCode: Number
    var statusMessage: String
    var request: Request
    var body: Any
    var caseless: caseless.Caseless
    fun toJSON(): ResponseAsJSON
    var timingStart: Number?
        get() = definedExternally
        set(value) = definedExternally
    var elapsedTime: Number?
        get() = definedExternally
        set(value) = definedExternally
    var timings: `T$4`?
        get() = definedExternally
        set(value) = definedExternally
    var timingPhases: `T$5`?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Headers {
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
}

external interface AuthOptions {
    var user: String?
        get() = definedExternally
        set(value) = definedExternally
    var username: String?
        get() = definedExternally
        set(value) = definedExternally
    var pass: String?
        get() = definedExternally
        set(value) = definedExternally
    var password: String?
        get() = definedExternally
        set(value) = definedExternally
    var sendImmediately: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var bearer: dynamic /* String | () -> String */
        get() = definedExternally
        set(value) = definedExternally
}

external interface OAuthOptions {
    var callback: String?
        get() = definedExternally
        set(value) = definedExternally
    var consumer_key: String?
        get() = definedExternally
        set(value) = definedExternally
    var consumer_secret: String?
        get() = definedExternally
        set(value) = definedExternally
    var token: String?
        get() = definedExternally
        set(value) = definedExternally
    var token_secret: String?
        get() = definedExternally
        set(value) = definedExternally
    var transport_method: dynamic /* 'body' | 'header' | 'query' */
        get() = definedExternally
        set(value) = definedExternally
    var verifier: String?
        get() = definedExternally
        set(value) = definedExternally
    var body_hash: dynamic /* true | String */
        get() = definedExternally
        set(value) = definedExternally
}

external interface HawkOptions {
    var credentials: Any
}

external interface AWSOptions {
    var secret: String
    var bucket: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RequestAsJSON {
    var uri: Url
    var method: String
    var headers: Headers
}

external interface ResponseAsJSON {
    var statusCode: Number
    var body: Any
    var headers: Headers
    var request: RequestAsJSON
}

external interface CookieJar {
    fun setCookie(cookieOrStr: Cookie, uri: String, options: tough.CookieJar.SetCookieOptions? = definedExternally /* null */)
    fun setCookie(cookieOrStr: Cookie, uri: Url, options: tough.CookieJar.SetCookieOptions? = definedExternally /* null */)
    fun setCookie(cookieOrStr: String, uri: String, options: tough.CookieJar.SetCookieOptions? = definedExternally /* null */)
    fun setCookie(cookieOrStr: String, uri: Url, options: tough.CookieJar.SetCookieOptions? = definedExternally /* null */)
    fun getCookieString(uri: String): String
    fun getCookieString(uri: Url): String
    fun getCookies(uri: String): Array<Cookie>
    fun getCookies(uri: Url): Array<Cookie>
}