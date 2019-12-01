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

external interface TLSSocket

external open class Server {
    open fun once(event: String /* "secureConnection" */, listener: (tlsSocket: TLSSocket) -> Unit): Server /* this */
    open fun listeners(event: String): Array<Function<*>>
    open fun listeners(event: Any): Array<Function<*>>
}

typealias MessageListener = (message: Any, sendHandle: Any) -> Unit

external open class HttpServer2 : Server {
    override fun once(event: String /* "unknownProtocol" */, listener: (socket: TLSSocket) -> Unit): HttpServer2 /* this */
    override fun listeners(event: String /* "message" */): Array<MessageListener>
}