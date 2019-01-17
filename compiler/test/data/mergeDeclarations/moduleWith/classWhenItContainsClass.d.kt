package classWhenItContainsClass.ws

@JsModule("ws")
external open class WebSocket(address: String) : events.EventEmitter {
    open fun connect(): Boolean = definedExternally
    open class Server {
        open fun start(): Unit = definedExternally
    }
}
