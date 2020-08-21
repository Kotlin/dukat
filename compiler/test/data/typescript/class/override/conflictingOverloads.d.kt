// [test] conflictingOverloads.kt
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

external open class Message {
    open fun once(event: String /* "abort" | "timeout" | "close" | "drain" | "cancel" | "submit" | "start" | "end" | "finish" | "basta" */, listener: () -> Unit): Message /* this */
    open fun once(event: Any, listener: () -> Unit): Message /* this */
}

external interface Ping {
    fun ping(a: Number)
    fun ping(a: String)
    fun ping(a: Boolean)
}

external fun addListener(event: String /* "disconnect" | "online" */, listener: (worker: Ping) -> Unit): Message

external interface Options<A, B>

external interface Result<T>

external interface `T$0` {
    var publicKey: String
    var privateKey: String
}

external fun generate(options: Options<String /* "pem" | "der" | "der" */, String /* "pem" | "der" | "pem" | "der" */>): dynamic /* Result */

external interface `T$1` {
    var publicKey: String
    var privateKey: Any
}

external interface `T$2` {
    var publicKey: Any
    var privateKey: String
}

external interface `T$3` {
    var publicKey: Any
    var privateKey: Any
}