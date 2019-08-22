import kotlin.js.*

external abstract class A {
    fun f(eventListener: EventListenerX, stringListener: StringListenerX)
    fun f(eventListener: (EventX) -> Unit, stringListener: StringListenerX)
    fun f(eventListener: EventListenerX, stringListener: (String) -> String)
    fun f(eventListener: (EventX) -> Unit, stringListener: (String) -> String)
}

external interface EventListenerX {
    fun handleEvent(event: EventX)
}

external interface StringListenerX {
    fun handleString(string: String): String
}

external abstract class EventX
