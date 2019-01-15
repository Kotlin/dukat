external interface Shape
external interface Box : Shape
external interface BaseEvent {
    var data: dynamic /* String | Number */
    fun getDelegateTarget(): Shape
    fun getElement(): Element
}
external open class BoxStringEvent : BaseEvent {
    override var data: String = definedExternally
    override fun getDelegateTarget(): Box = definedExternally
    override fun getElement(): HTMLElement = definedExternally
}
external interface NumberEvent : BaseEvent {
    override var data: Number
}
