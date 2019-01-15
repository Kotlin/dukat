external open class BaseJQueryEventObject : Event {
    open var data: Any = definedExternally
    open var delegateTarget: Element = definedExternally
    open fun isDefaultPrevented(): Boolean = definedExternally
    open fun isImmediatePropogationStopped(): Boolean = definedExternally
    open fun isPropagationStopped(): Boolean = definedExternally
}
