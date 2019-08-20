
import kotlin.js.*

external abstract class A {
    fun f(eventListener: EventListener, stringListener: StringListener)
    fun f(eventListener: (Event) -> Unit, stringListener: StringListener)
    fun f(eventListener: EventListener, stringListener: (String) -> String)
    fun f(eventListener: (Event) -> Unit, stringListener: (String) -> String)
}

external interface EventListener {
    fun handleEvent(event: Event)
}

external interface StringListener {
    fun handleString(string: String): String
}

external abstract class Event
