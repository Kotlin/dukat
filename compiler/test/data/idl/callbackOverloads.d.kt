import kotlin.js.*
import org.khronos.webgl.*

public external abstract class A {
    fun f(eventListener: EventListenerX, stringListener: StringListenerX)
    fun f(eventListener: (EventX) -> Unit, stringListener: StringListenerX)
    fun f(eventListener: EventListenerX, stringListener: (String) -> String)
    fun f(eventListener: (EventX) -> Unit, stringListener: (String) -> String)
}

public external interface EventListenerX {
    fun handleEvent(event: EventX)
}

public external interface StringListenerX {
    fun handleString(string: String): String
}

public external abstract class EventX
