
import kotlin.js.*

external interface EventListenerX {
    fun handleEvent(event: EventX)
}
external abstract class EventX