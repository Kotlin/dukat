package missedOverloads

external interface MyXHR
external interface MyQuery
external interface MyEvent
external interface MyOptions
external interface JQueryStatic {
    fun get(url: String, success: (() -> Any)? = definedExternally /* null */, dataType: String? = definedExternally /* null */): MyXHR
    fun get(url: String, data: String? = definedExternally /* null */, success: (() -> Any)? = definedExternally /* null */, dataType: String? = definedExternally /* null */): MyXHR
    fun get(url: String, data: Any? = definedExternally /* null */, success: (() -> Any)? = definedExternally /* null */, dataType: String? = definedExternally /* null */): MyXHR
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
    fun get(url: String): MyXHR
    @nativeInvoke
    operator fun invoke(selector: String): MyQuery
    fun fadeTo(duration: String, opacity: Number): MyQuery
    fun fadeTo(duration: Number, opacity: Number): MyQuery
}
external open class JJ {
    open fun foo(data: String, context: HTMLElement? = definedExternally /* null */, keepScripts: Boolean? = definedExternally /* null */): Array<Any> = definedExternally
    open fun hide(duration: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
    open fun hide(duration: Number? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
    open fun hide(duration: String? = definedExternally /* null */, easing: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
    open fun hide(duration: Number? = definedExternally /* null */, easing: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
    open fun hide(options: MyOptions): MyQuery = definedExternally
    open fun trigger(eventType: String, extraParameters: Any? = definedExternally /* null */): MyQuery = definedExternally
    open fun trigger(eventType: String, extraParameters: Array<Any>? = definedExternally /* null */): MyQuery = definedExternally
    open fun trigger(event: MyEvent, extraParameters: Any? = definedExternally /* null */): MyQuery = definedExternally
    open fun trigger(event: MyEvent, extraParameters: Array<Any>? = definedExternally /* null */): MyQuery = definedExternally
    open fun hide(): MyQuery = definedExternally
    open fun trigger(eventType: String): MyQuery = definedExternally
    open fun trigger(event: MyEvent): MyQuery = definedExternally
}
external fun foo(data: String, context: HTMLElement? = definedExternally /* null */, keepScripts: Boolean? = definedExternally /* null */): Array<Any> = definedExternally
external fun hide(duration: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
external fun hide(duration: Number? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
external fun hide(duration: String? = definedExternally /* null */, easing: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
external fun hide(duration: Number? = definedExternally /* null */, easing: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): MyQuery = definedExternally
external fun hide(options: MyOptions): MyQuery = definedExternally
external fun trigger(eventType: String, extraParameters: Any? = definedExternally /* null */): MyQuery = definedExternally
external fun trigger(eventType: String, extraParameters: Array<Any>? = definedExternally /* null */): MyQuery = definedExternally
external fun trigger(event: MyEvent, extraParameters: Any? = definedExternally /* null */): MyQuery = definedExternally
external fun trigger(event: MyEvent, extraParameters: Array<Any>? = definedExternally /* null */): MyQuery = definedExternally
external fun hide(): MyQuery = definedExternally
external fun trigger(eventType: String): MyQuery = definedExternally
external fun trigger(event: MyEvent): MyQuery = definedExternally
