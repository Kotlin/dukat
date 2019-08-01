package org.w3c.workers

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
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.xhr.*

external abstract class ServiceWorkerRegistration : EventTarget {
    open val installing: ServiceWorker?
    open val waiting: ServiceWorker?
    open val active: ServiceWorker?
    open val scope: String
    open var onupdatefound: ((Event) -> dynamic)?
    open val APISpace: dynamic
    fun update(): Promise<Unit>
    fun unregister(): Promise<Boolean>
    fun methodName(): Promise<dynamic>
    fun showNotification(title: String, options: NotificationOptions = definedExternally): Promise<Unit>
    fun getNotifications(filter: GetNotificationOptions = definedExternally): Promise<dynamic>
}

external abstract class ServiceWorkerGlobalScope : WorkerGlobalScope {
    open val clients: Clients
    open val registration: ServiceWorkerRegistration
    open var oninstall: ((Event) -> dynamic)?
    open var onactivate: ((Event) -> dynamic)?
    open var onfetch: ((FetchEvent) -> dynamic)?
    open var onforeignfetch: ((Event) -> dynamic)?
    open var onmessage: ((MessageEvent) -> dynamic)?
    open var onfunctionalevent: ((Event) -> dynamic)?
    open var onnotificationclick: ((NotificationEvent) -> dynamic)?
    open var onnotificationclose: ((NotificationEvent) -> dynamic)?
    fun skipWaiting(): Promise<Unit>
}

external abstract class ServiceWorker : EventTarget, AbstractWorker, UnionMessagePortOrServiceWorker, UnionClientOrMessagePortOrServiceWorker {
    open val scriptURL: String
    open val state: ServiceWorkerState
    open var onstatechange: ((Event) -> dynamic)?
    fun postMessage(message: Any?, transfer: Array<dynamic> = definedExternally)
}

external abstract class ServiceWorkerContainer : EventTarget {
    open val controller: ServiceWorker?
    open val ready: Promise<ServiceWorkerRegistration>
    open var oncontrollerchange: ((Event) -> dynamic)?
    open var onmessage: ((MessageEvent) -> dynamic)?
    fun register(scriptURL: String, options: RegistrationOptions = definedExternally): Promise<ServiceWorkerRegistration>
    fun getRegistration(clientURL: String = definedExternally): Promise<Any?>
    fun getRegistrations(): Promise<dynamic>
    fun startMessages()
}

external interface RegistrationOptions {
    var scope: String?
        get() = definedExternally
        set(value) = definedExternally
    var type: WorkerType? /* = WorkerType.CLASSIC */
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun RegistrationOptions(scope: String? = undefined, type: WorkerType? = WorkerType.CLASSIC): RegistrationOptions {
    val o = js("({})")

    o["scope"] = scope
    o["type"] = type

    return o
}

external open class ServiceWorkerMessageEvent(type: String, eventInitDict: ServiceWorkerMessageEventInit = definedExternally) : Event {
    open val data: Any?
    open val origin: String
    open val lastEventId: String
    open val source: UnionMessagePortOrServiceWorker?
    open val ports: Array<out MessagePort>?

    companion object {
        val NONE: Short
        val CAPTURING_PHASE: Short
        val AT_TARGET: Short
        val BUBBLING_PHASE: Short
    }
}

external interface ServiceWorkerMessageEventInit : EventInit {
    var data: Any?
        get() = definedExternally
        set(value) = definedExternally
    var origin: String?
        get() = definedExternally
        set(value) = definedExternally
    var lastEventId: String?
        get() = definedExternally
        set(value) = definedExternally
    var source: UnionMessagePortOrServiceWorker?
        get() = definedExternally
        set(value) = definedExternally
    var ports: Array<MessagePort>?
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun ServiceWorkerMessageEventInit(data: Any? = undefined, origin: String? = undefined, lastEventId: String? = undefined, source: UnionMessagePortOrServiceWorker? = undefined, ports: Array<MessagePort>? = undefined, bubbles: Boolean? = false, cancelable: Boolean? = false, composed: Boolean? = false): ServiceWorkerMessageEventInit {
    val o = js("({})")

    o["data"] = data
    o["origin"] = origin
    o["lastEventId"] = lastEventId
    o["source"] = source
    o["ports"] = ports
    o["bubbles"] = bubbles
    o["cancelable"] = cancelable
    o["composed"] = composed

    return o
}

external abstract class Client : UnionClientOrMessagePortOrServiceWorker {
    open val url: String
    open val frameType: FrameType
    open val id: String
    fun postMessage(message: Any?, transfer: Array<dynamic> = definedExternally)
}

external abstract class WindowClient : Client {
    open val visibilityState: dynamic
    open val focused: Boolean
    fun focus(): Promise<WindowClient>
    fun navigate(url: String): Promise<WindowClient>
}

external abstract class Clients {
    fun get(id: String): Promise<Any?>
    fun matchAll(options: ClientQueryOptions = definedExternally): Promise<dynamic>
    fun openWindow(url: String): Promise<WindowClient?>
    fun claim(): Promise<Unit>
}

external interface ClientQueryOptions {
    var includeUncontrolled: Boolean? /* = false */
        get() = definedExternally
        set(value) = definedExternally
    var type: ClientType? /* = ClientType.WINDOW */
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun ClientQueryOptions(includeUncontrolled: Boolean? = false, type: ClientType? = ClientType.WINDOW): ClientQueryOptions {
    val o = js("({})")

    o["includeUncontrolled"] = includeUncontrolled
    o["type"] = type

    return o
}

external open class ExtendableEvent(type: String, eventInitDict: ExtendableEventInit = definedExternally) : Event {
    fun waitUntil(f: Promise<Any?>)
}

external interface ExtendableEventInit : EventInit {

    companion object {
        val NONE: Short
        val CAPTURING_PHASE: Short
        val AT_TARGET: Short
        val BUBBLING_PHASE: Short
    }
}

@kotlin.internal.InlineOnly
inline fun ExtendableEventInit(bubbles: Boolean? = false, cancelable: Boolean? = false, composed: Boolean? = false): ExtendableEventInit {
    val o = js("({})")

    o["bubbles"] = bubbles
    o["cancelable"] = cancelable
    o["composed"] = composed

    return o
}

external open class InstallEvent(type: String, eventInitDict: ExtendableEventInit = definedExternally) : ExtendableEvent {
    fun registerForeignFetch(options: ForeignFetchOptions)

    companion object {
        val NONE: Short
        val CAPTURING_PHASE: Short
        val AT_TARGET: Short
        val BUBBLING_PHASE: Short
    }
}

external interface ForeignFetchOptions {
    var scopes: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var origins: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun ForeignFetchOptions(scopes: Array<String>?, origins: Array<String>?): ForeignFetchOptions {
    val o = js("({})")

    o["scopes"] = scopes
    o["origins"] = origins

    return o
}

external open class FetchEvent(type: String, eventInitDict: FetchEventInit) : ExtendableEvent {
    open val request: Request
    open val clientId: String?
    open val isReload: Boolean
    fun respondWith(r: Promise<Response>)

    companion object {
        val NONE: Short
        val CAPTURING_PHASE: Short
        val AT_TARGET: Short
        val BUBBLING_PHASE: Short
    }
}

external interface FetchEventInit : ExtendableEventInit {
    var request: Request?
        get() = definedExternally
        set(value) = definedExternally
    var clientId: String? /* = null */
        get() = definedExternally
        set(value) = definedExternally
    var isReload: Boolean? /* = false */
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun FetchEventInit(request: Request?, clientId: String? = null, isReload: Boolean? = false, bubbles: Boolean? = false, cancelable: Boolean? = false, composed: Boolean? = false): FetchEventInit {
    val o = js("({})")

    o["request"] = request
    o["clientId"] = clientId
    o["isReload"] = isReload
    o["bubbles"] = bubbles
    o["cancelable"] = cancelable
    o["composed"] = composed

    return o
}

external open class ForeignFetchEvent(type: String, eventInitDict: ForeignFetchEventInit) : ExtendableEvent {
    open val request: Request
    open val origin: String
    fun respondWith(r: Promise<ForeignFetchResponse>)

    companion object {
        val NONE: Short
        val CAPTURING_PHASE: Short
        val AT_TARGET: Short
        val BUBBLING_PHASE: Short
    }
}

external interface ForeignFetchEventInit : ExtendableEventInit {
    var request: Request?
        get() = definedExternally
        set(value) = definedExternally
    var origin: String? /* = "null" */
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun ForeignFetchEventInit(request: Request?, origin: String? = "null", bubbles: Boolean? = false, cancelable: Boolean? = false, composed: Boolean? = false): ForeignFetchEventInit {
    val o = js("({})")

    o["request"] = request
    o["origin"] = origin
    o["bubbles"] = bubbles
    o["cancelable"] = cancelable
    o["composed"] = composed

    return o
}

external interface ForeignFetchResponse {
    var response: Response?
        get() = definedExternally
        set(value) = definedExternally
    var origin: String?
        get() = definedExternally
        set(value) = definedExternally
    var headers: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun ForeignFetchResponse(response: Response?, origin: String? = undefined, headers: Array<String>? = undefined): ForeignFetchResponse {
    val o = js("({})")

    o["response"] = response
    o["origin"] = origin
    o["headers"] = headers

    return o
}

external open class ExtendableMessageEvent(type: String, eventInitDict: ExtendableMessageEventInit = definedExternally) : ExtendableEvent {
    open val data: Any?
    open val origin: String
    open val lastEventId: String
    open val source: UnionClientOrMessagePortOrServiceWorker?
    open val ports: Array<out MessagePort>?

    companion object {
        val NONE: Short
        val CAPTURING_PHASE: Short
        val AT_TARGET: Short
        val BUBBLING_PHASE: Short
    }
}

external interface ExtendableMessageEventInit : ExtendableEventInit {
    var data: Any?
        get() = definedExternally
        set(value) = definedExternally
    var origin: String?
        get() = definedExternally
        set(value) = definedExternally
    var lastEventId: String?
        get() = definedExternally
        set(value) = definedExternally
    var source: UnionClientOrMessagePortOrServiceWorker?
        get() = definedExternally
        set(value) = definedExternally
    var ports: Array<MessagePort>?
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun ExtendableMessageEventInit(data: Any? = undefined, origin: String? = undefined, lastEventId: String? = undefined, source: UnionClientOrMessagePortOrServiceWorker? = undefined, ports: Array<MessagePort>? = undefined, bubbles: Boolean? = false, cancelable: Boolean? = false, composed: Boolean? = false): ExtendableMessageEventInit {
    val o = js("({})")

    o["data"] = data
    o["origin"] = origin
    o["lastEventId"] = lastEventId
    o["source"] = source
    o["ports"] = ports
    o["bubbles"] = bubbles
    o["cancelable"] = cancelable
    o["composed"] = composed

    return o
}

external abstract class Cache {
    fun match(request: dynamic, options: CacheQueryOptions = definedExternally): Promise<Any?>
    fun matchAll(request: dynamic = definedExternally, options: CacheQueryOptions = definedExternally): Promise<dynamic>
    fun add(request: dynamic): Promise<Unit>
    fun addAll(requests: Array<dynamic>): Promise<Unit>
    fun put(request: dynamic, response: Response): Promise<Unit>
    fun delete(request: dynamic, options: CacheQueryOptions = definedExternally): Promise<Boolean>
    fun keys(request: dynamic = definedExternally, options: CacheQueryOptions = definedExternally): Promise<dynamic>
}

external interface CacheQueryOptions {
    var ignoreSearch: Boolean? /* = false */
        get() = definedExternally
        set(value) = definedExternally
    var ignoreMethod: Boolean? /* = false */
        get() = definedExternally
        set(value) = definedExternally
    var ignoreVary: Boolean? /* = false */
        get() = definedExternally
        set(value) = definedExternally
    var cacheName: String?
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun CacheQueryOptions(ignoreSearch: Boolean? = false, ignoreMethod: Boolean? = false, ignoreVary: Boolean? = false, cacheName: String? = undefined): CacheQueryOptions {
    val o = js("({})")

    o["ignoreSearch"] = ignoreSearch
    o["ignoreMethod"] = ignoreMethod
    o["ignoreVary"] = ignoreVary
    o["cacheName"] = cacheName

    return o
}

external interface CacheBatchOperation {
    var type: String?
        get() = definedExternally
        set(value) = definedExternally
    var request: Request?
        get() = definedExternally
        set(value) = definedExternally
    var response: Response?
        get() = definedExternally
        set(value) = definedExternally
    var options: CacheQueryOptions?
        get() = definedExternally
        set(value) = definedExternally
}

@kotlin.internal.InlineOnly
inline fun CacheBatchOperation(type: String? = undefined, request: Request? = undefined, response: Response? = undefined, options: CacheQueryOptions? = undefined): CacheBatchOperation {
    val o = js("({})")

    o["type"] = type
    o["request"] = request
    o["response"] = response
    o["options"] = options

    return o
}

external abstract class CacheStorage {
    fun match(request: dynamic, options: CacheQueryOptions = definedExternally): Promise<Any?>
    fun has(cacheName: String): Promise<Boolean>
    fun open(cacheName: String): Promise<Cache>
    fun delete(cacheName: String): Promise<Boolean>
    fun keys(): Promise<dynamic>
}

external open class FunctionalEvent : ExtendableEvent {

    companion object {
        val NONE: Short
        val CAPTURING_PHASE: Short
        val AT_TARGET: Short
        val BUBBLING_PHASE: Short
    }
}

external @marker interface UnionClientOrMessagePortOrServiceWorker

/* please, don't implement this interface! */
@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface ServiceWorkerState {
    companion object
}
inline val ServiceWorkerState.Companion.INSTALLING: ServiceWorkerState get() = "installing".asDynamic().unsafeCast<ServiceWorkerState>()
inline val ServiceWorkerState.Companion.INSTALLED: ServiceWorkerState get() = "installed".asDynamic().unsafeCast<ServiceWorkerState>()
inline val ServiceWorkerState.Companion.ACTIVATING: ServiceWorkerState get() = "activating".asDynamic().unsafeCast<ServiceWorkerState>()
inline val ServiceWorkerState.Companion.ACTIVATED: ServiceWorkerState get() = "activated".asDynamic().unsafeCast<ServiceWorkerState>()
inline val ServiceWorkerState.Companion.REDUNDANT: ServiceWorkerState get() = "redundant".asDynamic().unsafeCast<ServiceWorkerState>()

/* please, don't implement this interface! */
@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface FrameType {
    companion object
}
inline val FrameType.Companion.AUXILIARY: FrameType get() = "auxiliary".asDynamic().unsafeCast<FrameType>()
inline val FrameType.Companion.TOP_LEVEL: FrameType get() = "top-level".asDynamic().unsafeCast<FrameType>()
inline val FrameType.Companion.NESTED: FrameType get() = "nested".asDynamic().unsafeCast<FrameType>()
inline val FrameType.Companion.NONE: FrameType get() = "none".asDynamic().unsafeCast<FrameType>()

/* please, don't implement this interface! */
@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface ClientType {
    companion object
}
inline val ClientType.Companion.WINDOW: ClientType get() = "window".asDynamic().unsafeCast<ClientType>()
inline val ClientType.Companion.WORKER: ClientType get() = "worker".asDynamic().unsafeCast<ClientType>()
inline val ClientType.Companion.SHAREDWORKER: ClientType get() = "sharedworker".asDynamic().unsafeCast<ClientType>()
inline val ClientType.Companion.ALL: ClientType get() = "all".asDynamic().unsafeCast<ClientType>()

