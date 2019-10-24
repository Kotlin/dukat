@file:JsModule("cluster")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package cluster

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

external interface ClusterSettings {
    var execArgv: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var exec: String?
        get() = definedExternally
        set(value) = definedExternally
    var args: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var silent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stdio: Array<Any>?
        get() = definedExternally
        set(value) = definedExternally
    var uid: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gid: Number?
        get() = definedExternally
        set(value) = definedExternally
    var inspectPort: dynamic /* Number | () -> Number */
        get() = definedExternally
        set(value) = definedExternally
}

external interface Address {
    var address: String
    var port: Number
    var addressType: dynamic /* Number | "udp4" | "udp6" */
}

external open class Worker : events.EventEmitter {
    open var id: Number
    open var process: child.ChildProcess
    open fun send(message: Any, sendHandle: Any? = definedExternally /* null */, callback: ((error: Error?) -> Unit)? = definedExternally /* null */): Boolean
    open fun kill(signal: String? = definedExternally /* null */)
    open fun destroy(signal: String? = definedExternally /* null */)
    open fun disconnect()
    open fun isConnected(): Boolean
    open fun isDead(): Boolean
    open var exitedAfterDisconnect: Boolean
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Worker /* this */
    open fun addListener(event: String /* "disconnect" */, listener: () -> Unit): Worker /* this */
    open fun addListener(event: String /* "error" */, listener: (error: Error) -> Unit): Worker /* this */
    open fun addListener(event: String /* "exit" */, listener: (code: Number, signal: String) -> Unit): Worker /* this */
    open fun addListener(event: String /* "listening" */, listener: (address: Address) -> Unit): Worker /* this */
    open fun addListener(event: String /* "message" */, listener: (message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Worker /* this */
    open fun addListener(event: String /* "online" */, listener: () -> Unit): Worker /* this */
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun emit(event: String /* "disconnect" */): Boolean
    open fun emit(event: String /* "error" */, error: Error): Boolean
    open fun emit(event: String /* "exit" */, code: Number, signal: String): Boolean
    open fun emit(event: String /* "listening" */, address: Address): Boolean
    open fun emit(event: String /* "message" */, message: Any, handle: net.Socket): Boolean
    open fun emit(event: String /* "message" */, message: Any, handle: net.Server): Boolean
    open fun emit(event: String /* "online" */): Boolean
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): Worker /* this */
    open fun on(event: String /* "disconnect" */, listener: () -> Unit): Worker /* this */
    open fun on(event: String /* "error" */, listener: (error: Error) -> Unit): Worker /* this */
    open fun on(event: String /* "exit" */, listener: (code: Number, signal: String) -> Unit): Worker /* this */
    open fun on(event: String /* "listening" */, listener: (address: Address) -> Unit): Worker /* this */
    open fun on(event: String /* "message" */, listener: (message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Worker /* this */
    open fun on(event: String /* "online" */, listener: () -> Unit): Worker /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): Worker /* this */
    open fun once(event: String /* "disconnect" */, listener: () -> Unit): Worker /* this */
    open fun once(event: String /* "error" */, listener: (error: Error) -> Unit): Worker /* this */
    open fun once(event: String /* "exit" */, listener: (code: Number, signal: String) -> Unit): Worker /* this */
    open fun once(event: String /* "listening" */, listener: (address: Address) -> Unit): Worker /* this */
    open fun once(event: String /* "message" */, listener: (message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Worker /* this */
    open fun once(event: String /* "online" */, listener: () -> Unit): Worker /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Worker /* this */
    open fun prependListener(event: String /* "disconnect" */, listener: () -> Unit): Worker /* this */
    open fun prependListener(event: String /* "error" */, listener: (error: Error) -> Unit): Worker /* this */
    open fun prependListener(event: String /* "exit" */, listener: (code: Number, signal: String) -> Unit): Worker /* this */
    open fun prependListener(event: String /* "listening" */, listener: (address: Address) -> Unit): Worker /* this */
    open fun prependListener(event: String /* "message" */, listener: (message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Worker /* this */
    open fun prependListener(event: String /* "online" */, listener: () -> Unit): Worker /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Worker /* this */
    open fun prependOnceListener(event: String /* "disconnect" */, listener: () -> Unit): Worker /* this */
    open fun prependOnceListener(event: String /* "error" */, listener: (error: Error) -> Unit): Worker /* this */
    open fun prependOnceListener(event: String /* "exit" */, listener: (code: Number, signal: String) -> Unit): Worker /* this */
    open fun prependOnceListener(event: String /* "listening" */, listener: (address: Address) -> Unit): Worker /* this */
    open fun prependOnceListener(event: String /* "message" */, listener: (message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Worker /* this */
    open fun prependOnceListener(event: String /* "online" */, listener: () -> Unit): Worker /* this */
}

external interface `T$0` {
    @nativeGetter
    operator fun get(index: String): Worker?
    @nativeSetter
    operator fun set(index: String, value: Worker?)
}

external interface Cluster : events.EventEmitter {
    var Worker: Worker
    fun disconnect(callback: (() -> Unit)? = definedExternally /* null */)
    fun fork(env: Any? = definedExternally /* null */): Worker
    var isMaster: Boolean
    var isWorker: Boolean
    var settings: ClusterSettings
    fun setupMaster(settings: ClusterSettings? = definedExternally /* null */)
    var worker: Worker?
        get() = definedExternally
        set(value) = definedExternally
    var workers: `T$0`?
        get() = definedExternally
        set(value) = definedExternally
    fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Cluster /* this */
    fun addListener(event: String /* "disconnect" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun addListener(event: String /* "exit" */, listener: (worker: Worker, code: Number, signal: String) -> Unit): Cluster /* this */
    fun addListener(event: String /* "fork" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun addListener(event: String /* "listening" */, listener: (worker: Worker, address: Address) -> Unit): Cluster /* this */
    fun addListener(event: String /* "message" */, listener: (worker: Worker, message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Cluster /* this */
    fun addListener(event: String /* "online" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun addListener(event: String /* "setup" */, listener: (settings: ClusterSettings) -> Unit): Cluster /* this */
    fun emit(event: String, vararg args: Any): Boolean
    fun emit(event: Any, vararg args: Any): Boolean
    fun emit(event: String /* "disconnect" */, worker: Worker): Boolean
    fun emit(event: String /* "exit" */, worker: Worker, code: Number, signal: String): Boolean
    fun emit(event: String /* "fork" */, worker: Worker): Boolean
    fun emit(event: String /* "listening" */, worker: Worker, address: Address): Boolean
    fun emit(event: String /* "message" */, worker: Worker, message: Any, handle: net.Socket): Boolean
    fun emit(event: String /* "message" */, worker: Worker, message: Any, handle: net.Server): Boolean
    fun emit(event: String /* "online" */, worker: Worker): Boolean
    fun emit(event: String /* "setup" */, settings: ClusterSettings): Boolean
    fun on(event: String, listener: (args: Array<Any>) -> Unit): Cluster /* this */
    fun on(event: String /* "disconnect" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun on(event: String /* "exit" */, listener: (worker: Worker, code: Number, signal: String) -> Unit): Cluster /* this */
    fun on(event: String /* "fork" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun on(event: String /* "listening" */, listener: (worker: Worker, address: Address) -> Unit): Cluster /* this */
    fun on(event: String /* "message" */, listener: (worker: Worker, message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Cluster /* this */
    fun on(event: String /* "online" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun on(event: String /* "setup" */, listener: (settings: ClusterSettings) -> Unit): Cluster /* this */
    fun once(event: String, listener: (args: Array<Any>) -> Unit): Cluster /* this */
    fun once(event: String /* "disconnect" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun once(event: String /* "exit" */, listener: (worker: Worker, code: Number, signal: String) -> Unit): Cluster /* this */
    fun once(event: String /* "fork" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun once(event: String /* "listening" */, listener: (worker: Worker, address: Address) -> Unit): Cluster /* this */
    fun once(event: String /* "message" */, listener: (worker: Worker, message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Cluster /* this */
    fun once(event: String /* "online" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun once(event: String /* "setup" */, listener: (settings: ClusterSettings) -> Unit): Cluster /* this */
    fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Cluster /* this */
    fun prependListener(event: String /* "disconnect" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun prependListener(event: String /* "exit" */, listener: (worker: Worker, code: Number, signal: String) -> Unit): Cluster /* this */
    fun prependListener(event: String /* "fork" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun prependListener(event: String /* "listening" */, listener: (worker: Worker, address: Address) -> Unit): Cluster /* this */
    fun prependListener(event: String /* "message" */, listener: (worker: Worker, message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Cluster /* this */
    fun prependListener(event: String /* "online" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun prependListener(event: String /* "setup" */, listener: (settings: ClusterSettings) -> Unit): Cluster /* this */
    fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Cluster /* this */
    fun prependOnceListener(event: String /* "disconnect" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun prependOnceListener(event: String /* "exit" */, listener: (worker: Worker, code: Number, signal: String) -> Unit): Cluster /* this */
    fun prependOnceListener(event: String /* "fork" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun prependOnceListener(event: String /* "listening" */, listener: (worker: Worker, address: Address) -> Unit): Cluster /* this */
    fun prependOnceListener(event: String /* "message" */, listener: (worker: Worker, message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Cluster /* this */
    fun prependOnceListener(event: String /* "online" */, listener: (worker: Worker) -> Unit): Cluster /* this */
    fun prependOnceListener(event: String /* "setup" */, listener: (settings: ClusterSettings) -> Unit): Cluster /* this */
}

external fun disconnect(callback: (() -> Unit)? = definedExternally /* null */)

external fun fork(env: Any? = definedExternally /* null */): Worker

external var isMaster: Boolean

external var isWorker: Boolean

external var settings: ClusterSettings

external fun setupMaster(settings: ClusterSettings? = definedExternally /* null */)

external var worker: Worker

external object workers {
    @nativeGetter
    operator fun get(index: String): Worker?
    @nativeSetter
    operator fun set(index: String, value: Worker?)
}

external fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Cluster

external fun addListener(event: String /* "disconnect" */, listener: (worker: Worker) -> Unit): Cluster

external fun addListener(event: String /* "exit" */, listener: (worker: Worker, code: Number, signal: String) -> Unit): Cluster

external fun addListener(event: String /* "fork" */, listener: (worker: Worker) -> Unit): Cluster

external fun addListener(event: String /* "listening" */, listener: (worker: Worker, address: Address) -> Unit): Cluster

external fun addListener(event: String /* "message" */, listener: (worker: Worker, message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Cluster

external fun addListener(event: String /* "online" */, listener: (worker: Worker) -> Unit): Cluster

external fun addListener(event: String /* "setup" */, listener: (settings: ClusterSettings) -> Unit): Cluster

external fun emit(event: String, vararg args: Any): Boolean

external fun emit(event: Any, vararg args: Any): Boolean

external fun emit(event: String /* "disconnect" */, worker: Worker): Boolean

external fun emit(event: String /* "exit" */, worker: Worker, code: Number, signal: String): Boolean

external fun emit(event: String /* "fork" */, worker: Worker): Boolean

external fun emit(event: String /* "listening" */, worker: Worker, address: Address): Boolean

external fun emit(event: String /* "message" */, worker: Worker, message: Any, handle: net.Socket): Boolean

external fun emit(event: String /* "message" */, worker: Worker, message: Any, handle: net.Server): Boolean

external fun emit(event: String /* "online" */, worker: Worker): Boolean

external fun emit(event: String /* "setup" */, settings: ClusterSettings): Boolean

external fun on(event: String, listener: (args: Array<Any>) -> Unit): Cluster

external fun on(event: String /* "disconnect" */, listener: (worker: Worker) -> Unit): Cluster

external fun on(event: String /* "exit" */, listener: (worker: Worker, code: Number, signal: String) -> Unit): Cluster

external fun on(event: String /* "fork" */, listener: (worker: Worker) -> Unit): Cluster

external fun on(event: String /* "listening" */, listener: (worker: Worker, address: Address) -> Unit): Cluster

external fun on(event: String /* "message" */, listener: (worker: Worker, message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Cluster

external fun on(event: String /* "online" */, listener: (worker: Worker) -> Unit): Cluster

external fun on(event: String /* "setup" */, listener: (settings: ClusterSettings) -> Unit): Cluster

external fun once(event: String, listener: (args: Array<Any>) -> Unit): Cluster

external fun once(event: String /* "disconnect" */, listener: (worker: Worker) -> Unit): Cluster

external fun once(event: String /* "exit" */, listener: (worker: Worker, code: Number, signal: String) -> Unit): Cluster

external fun once(event: String /* "fork" */, listener: (worker: Worker) -> Unit): Cluster

external fun once(event: String /* "listening" */, listener: (worker: Worker, address: Address) -> Unit): Cluster

external fun once(event: String /* "message" */, listener: (worker: Worker, message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Cluster

external fun once(event: String /* "online" */, listener: (worker: Worker) -> Unit): Cluster

external fun once(event: String /* "setup" */, listener: (settings: ClusterSettings) -> Unit): Cluster

external fun removeListener(event: String, listener: (args: Array<Any>) -> Unit): Cluster

external fun removeAllListeners(event: String? = definedExternally /* null */): Cluster

external fun setMaxListeners(n: Number): Cluster

external fun getMaxListeners(): Number

external fun listeners(event: String): Array<Function<*>>

external fun listenerCount(type: String): Number

external fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): Cluster

external fun prependListener(event: String /* "disconnect" */, listener: (worker: Worker) -> Unit): Cluster

external fun prependListener(event: String /* "exit" */, listener: (worker: Worker, code: Number, signal: String) -> Unit): Cluster

external fun prependListener(event: String /* "fork" */, listener: (worker: Worker) -> Unit): Cluster

external fun prependListener(event: String /* "listening" */, listener: (worker: Worker, address: Address) -> Unit): Cluster

external fun prependListener(event: String /* "message" */, listener: (worker: Worker, message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Cluster

external fun prependListener(event: String /* "online" */, listener: (worker: Worker) -> Unit): Cluster

external fun prependListener(event: String /* "setup" */, listener: (settings: ClusterSettings) -> Unit): Cluster

external fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): Cluster

external fun prependOnceListener(event: String /* "disconnect" */, listener: (worker: Worker) -> Unit): Cluster

external fun prependOnceListener(event: String /* "exit" */, listener: (worker: Worker, code: Number, signal: String) -> Unit): Cluster

external fun prependOnceListener(event: String /* "fork" */, listener: (worker: Worker) -> Unit): Cluster

external fun prependOnceListener(event: String /* "listening" */, listener: (worker: Worker, address: Address) -> Unit): Cluster

external fun prependOnceListener(event: String /* "message" */, listener: (worker: Worker, message: Any, handle: dynamic /* net.Socket | net.Server */) -> Unit): Cluster

external fun prependOnceListener(event: String /* "online" */, listener: (worker: Worker) -> Unit): Cluster

external fun prependOnceListener(event: String /* "setup" */, listener: (settings: ClusterSettings) -> Unit): Cluster

external fun eventNames(): Array<String>