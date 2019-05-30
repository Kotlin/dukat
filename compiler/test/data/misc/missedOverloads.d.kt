@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")

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

external interface MyXHR
external interface MyQuery
external interface MyEvent
external interface MyOptions
external interface JQueryStatic {
    fun get(url: String, success: (() -> Any)? = definedExternally /* null */, dataType: String? = definedExternally /* null */): MyXHR
    fun get(url: String, data: Any? = definedExternally /* null */, success: (() -> Any)? = definedExternally /* null */, dataType: String? = definedExternally /* null */): MyXHR
    fun get(url: String, data: String? = definedExternally /* null */, success: (() -> Any)? = definedExternally /* null */, dataType: String? = definedExternally /* null */): MyXHR
    fun get(settings: MyOptions): MyXHR
    @nativeInvoke
    operator fun invoke(selector: String, context: Element? = definedExternally /* null */): MyQuery
    @nativeInvoke
    operator fun invoke(selector: String, context: MyQuery? = definedExternally /* null */): MyQuery
    @nativeInvoke
    operator fun invoke(element: Element): MyQuery
    @nativeInvoke
    operator fun invoke(): MyQuery
    @nativeInvoke
    operator fun invoke(html: String, ownerDocument: Document? = definedExternally /* null */): MyQuery
    @nativeInvoke
    operator fun invoke(html: String, attributes: Any): MyQuery
    fun fadeTo(duration: String, opacity: Number, complete: Function<*>? = definedExternally /* null */): MyQuery
    fun fadeTo(duration: Number, opacity: Number, complete: Function<*>? = definedExternally /* null */): MyQuery
    fun fadeTo(duration: String, opacity: Number, easing: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery
    fun fadeTo(duration: Number, opacity: Number, easing: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery
    @nativeInvoke
    operator fun invoke(selector: String): MyQuery
}
external open class JJ {
    open fun foo(data: String, context: HTMLElement? = definedExternally /* null */, keepScripts: Boolean? = definedExternally /* null */): Array<Any> = definedExternally
    open fun hide(duration: Number? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
    open fun hide(duration: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
    open fun hide(duration: Number? = definedExternally /* null */, easing: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
    open fun hide(duration: String? = definedExternally /* null */, easing: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
    open fun hide(options: MyOptions): MyQuery = definedExternally
    open fun trigger(eventType: String, extraParameters: Array<Any>? = definedExternally /* null */): MyQuery = definedExternally
    open fun trigger(eventType: String, extraParameters: Any? = definedExternally /* null */): MyQuery = definedExternally
    open fun trigger(event: MyEvent, extraParameters: Array<Any>? = definedExternally /* null */): MyQuery = definedExternally
    open fun trigger(event: MyEvent, extraParameters: Any? = definedExternally /* null */): MyQuery = definedExternally
    open fun hide(): MyQuery = definedExternally
    open fun trigger(eventType: String): MyQuery = definedExternally
    open fun trigger(event: MyEvent): MyQuery = definedExternally
}
external fun foo(data: String, context: HTMLElement? = definedExternally /* null */, keepScripts: Boolean? = definedExternally /* null */): Array<Any> = definedExternally
external fun hide(duration: Number? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
external fun hide(duration: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
external fun hide(duration: Number? = definedExternally /* null */, easing: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
external fun hide(duration: String? = definedExternally /* null */, easing: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
external fun hide(options: MyOptions): MyQuery = definedExternally
external fun trigger(eventType: String, extraParameters: Array<Any>? = definedExternally /* null */): MyQuery = definedExternally
external fun trigger(eventType: String, extraParameters: Any? = definedExternally /* null */): MyQuery = definedExternally
external fun trigger(event: MyEvent, extraParameters: Array<Any>? = definedExternally /* null */): MyQuery = definedExternally
external fun trigger(event: MyEvent, extraParameters: Any? = definedExternally /* null */): MyQuery = definedExternally
external fun hide(): MyQuery = definedExternally
external fun trigger(eventType: String): MyQuery = definedExternally
external fun trigger(event: MyEvent): MyQuery = definedExternally
