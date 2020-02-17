import kotlin.js.*
import org.khronos.webgl.*

public external abstract class A {
    open var event1: (A?, Int) -> Unit
    open var event2: ((A?, Int) -> Unit)?
    open var event3: () -> dynamic
}