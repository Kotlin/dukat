import kotlin.js.*

public external interface EventListenerX {
    fun handleEvent(event: EventX)
}

public external abstract class EventX
