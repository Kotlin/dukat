// [test] missedOverloads.kt
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

external interface MyXHR

external interface MyQuery

external interface MyEvent

external interface MyOptions

external interface JQueryStatic {
    fun get(url: String, success: () -> Any = definedExternally, dataType: String = definedExternally): MyXHR
    fun get(url: String, data: Any = definedExternally, success: () -> Any = definedExternally, dataType: String = definedExternally): MyXHR
    fun get(url: String, data: String = definedExternally, success: () -> Any = definedExternally, dataType: String = definedExternally): MyXHR
    fun get(settings: MyOptions): MyXHR
    @nativeInvoke
    operator fun invoke(selector: String, context: Element = definedExternally): MyQuery
    @nativeInvoke
    operator fun invoke(selector: String, context: MyQuery = definedExternally): MyQuery
    @nativeInvoke
    operator fun invoke(element: Element): MyQuery
    @nativeInvoke
    operator fun invoke(): MyQuery
    @nativeInvoke
    operator fun invoke(html: String, ownerDocument: Document = definedExternally): MyQuery
    @nativeInvoke
    operator fun invoke(html: String, attributes: Any): MyQuery
    fun fadeTo(duration: String, opacity: Number, complete: Function<*> = definedExternally): MyQuery
    fun fadeTo(duration: Number, opacity: Number, complete: Function<*> = definedExternally): MyQuery
    fun fadeTo(duration: String, opacity: Number, easing: String = definedExternally, complete: Function<*> = definedExternally): MyQuery
    fun fadeTo(duration: Number, opacity: Number, easing: String = definedExternally, complete: Function<*> = definedExternally): MyQuery
    @nativeInvoke
    operator fun invoke(selector: String): MyQuery
}

external open class JJ {
    open fun foo(data: String, context: HTMLElement = definedExternally, keepScripts: Boolean = definedExternally): Array<Any>
    open fun hide(duration: Number = definedExternally, complete: Function<*> = definedExternally): MyQuery
    open fun hide(duration: String = definedExternally, complete: Function<*> = definedExternally): MyQuery
    open fun hide(duration: Number = definedExternally, easing: String = definedExternally, complete: Function<*> = definedExternally): MyQuery
    open fun hide(duration: String = definedExternally, easing: String = definedExternally, complete: Function<*> = definedExternally): MyQuery
    open fun hide(options: MyOptions): MyQuery
    open fun trigger(eventType: String, extraParameters: Array<Any> = definedExternally): MyQuery
    open fun trigger(eventType: String, extraParameters: Any = definedExternally): MyQuery
    open fun trigger(event: MyEvent, extraParameters: Array<Any> = definedExternally): MyQuery
    open fun trigger(event: MyEvent, extraParameters: Any = definedExternally): MyQuery
    open fun hide(): MyQuery
    open fun trigger(eventType: String): MyQuery
    open fun trigger(event: MyEvent): MyQuery
}

external fun foo(data: String, context: HTMLElement = definedExternally, keepScripts: Boolean = definedExternally): Array<Any>

external fun hide(duration: Number = definedExternally, complete: Function<*> = definedExternally): MyQuery

external fun hide(duration: String = definedExternally, complete: Function<*> = definedExternally): MyQuery

external fun hide(duration: Number = definedExternally, easing: String = definedExternally, complete: Function<*> = definedExternally): MyQuery

external fun hide(duration: String = definedExternally, easing: String = definedExternally, complete: Function<*> = definedExternally): MyQuery

external fun hide(options: MyOptions): MyQuery

external fun trigger(eventType: String, extraParameters: Array<Any> = definedExternally): MyQuery

external fun trigger(eventType: String, extraParameters: Any = definedExternally): MyQuery

external fun trigger(event: MyEvent, extraParameters: Array<Any> = definedExternally): MyQuery

external fun trigger(event: MyEvent, extraParameters: Any = definedExternally): MyQuery

external fun hide(): MyQuery

external fun trigger(eventType: String): MyQuery

external fun trigger(event: MyEvent): MyQuery