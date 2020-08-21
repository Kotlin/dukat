@file:JsModule("https")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package https

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

external interface `T$0` {
    var rejectUnauthorized: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var servername: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AgentOptions : http.AgentOptions, tls.ConnectionOptions {
    var rejectUnauthorized: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var maxCachedSessions: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external open class Agent(options: AgentOptions? = definedExternally /* null */) : http.Agent {
    open var options: AgentOptions
}

external open class Server(requestListener: http.RequestListener? = definedExternally /* null */) : tls.Server {
    constructor(options: tls.SecureContextOptions, requestListener: http.RequestListener?)
    open fun setTimeout(callback: () -> Unit): Server /* this */
    open fun setTimeout(msecs: Number? = definedExternally /* null */, callback: (() -> Unit)? = definedExternally /* null */): Server /* this */
    open var maxHeadersCount: Number?
    open var timeout: Number
    open var headersTimeout: Number
    open var keepAliveTimeout: Number
}

external fun createServer(requestListener: http.RequestListener? = definedExternally /* null */): Server

external fun createServer(options: tls.SecureContextOptions /* tls.SecureContextOptions & tls.TlsOptions & http.ServerOptions */, requestListener: http.RequestListener? = definedExternally /* null */): Server

external fun request(options: http.RequestOptions /* http.RequestOptions & tls.SecureContextOptions & `T$0` */, callback: ((res: http.IncomingMessage) -> Unit)? = definedExternally /* null */): http.ClientRequest

external fun request(options: String, callback: ((res: http.IncomingMessage) -> Unit)? = definedExternally /* null */): http.ClientRequest

external fun request(options: URL, callback: ((res: http.IncomingMessage) -> Unit)? = definedExternally /* null */): http.ClientRequest

external fun request(url: String, options: http.RequestOptions /* http.RequestOptions & tls.SecureContextOptions & `T$0` */, callback: ((res: http.IncomingMessage) -> Unit)? = definedExternally /* null */): http.ClientRequest

external fun request(url: URL, options: http.RequestOptions /* http.RequestOptions & tls.SecureContextOptions & `T$0` */, callback: ((res: http.IncomingMessage) -> Unit)? = definedExternally /* null */): http.ClientRequest

external fun get(options: http.RequestOptions /* http.RequestOptions & tls.SecureContextOptions & `T$0` */, callback: ((res: http.IncomingMessage) -> Unit)? = definedExternally /* null */): http.ClientRequest

external fun get(options: String, callback: ((res: http.IncomingMessage) -> Unit)? = definedExternally /* null */): http.ClientRequest

external fun get(options: URL, callback: ((res: http.IncomingMessage) -> Unit)? = definedExternally /* null */): http.ClientRequest

external fun get(url: String, options: http.RequestOptions /* http.RequestOptions & tls.SecureContextOptions & `T$0` */, callback: ((res: http.IncomingMessage) -> Unit)? = definedExternally /* null */): http.ClientRequest

external fun get(url: URL, options: http.RequestOptions /* http.RequestOptions & tls.SecureContextOptions & `T$0` */, callback: ((res: http.IncomingMessage) -> Unit)? = definedExternally /* null */): http.ClientRequest

external var globalAgent: Agent