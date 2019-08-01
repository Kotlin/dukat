
import kotlin.js.*

external abstract class A {
    var event1: (A?, Int) -> Unit
    var event2: ((A?, Int) -> Unit)?
    var event3: () -> dynamic
}