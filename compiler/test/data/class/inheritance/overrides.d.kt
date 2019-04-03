package overrides

external interface Shape
external interface Box : Shape
external interface BaseEvent {
    var data: dynamic /* String | Number */
    fun getDelegateTarget(): Shape
    fun getElement(): Element
    fun <T : Shape> transform(shape: T? = definedExternally /* null */): T
}
external open class BoxStringEvent : BaseEvent {
    override var data: String = definedExternally
    override fun getDelegateTarget(): Box = definedExternally
    override fun getElement(): HTMLElement = definedExternally
    override fun <T : Shape> transform(shape: T?): T = definedExternally
}
external interface NumberEvent : BaseEvent {
    override var data: Number
}
