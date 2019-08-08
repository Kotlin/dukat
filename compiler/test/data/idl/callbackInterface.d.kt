
import kotlin.js.*

external interface EventListener {
    fun handleEvent(event: Event)
}

external abstract class Event