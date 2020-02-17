import kotlin.js.*
import org.khronos.webgl.*

public external interface EventListenerX {
    fun handleEvent(event: EventX)
}

public external abstract class EventX
