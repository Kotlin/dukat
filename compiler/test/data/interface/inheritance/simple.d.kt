package simple

external interface BaseJQueryEventObject : Event {
    var data: Any
    var delegateTarget: Element
    fun isDefaultPrevented(): Boolean
    fun isImmediatePropogationStopped(): Boolean
    fun isPropagationStopped(): Boolean
}
