import kotlin.js.*
import org.khronos.webgl.*

public external open class A(a: Int) {
    var x: Int
    var y: Int
    fun g()
    fun f()

    companion object {
        val z: Int
    }
}

public external interface B {
    var a: Boolean? /* = true */
    var b: Boolean? /* = false */
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline fun B(a: Boolean? = true, b: Boolean? = false): B {
    val o = js("({})")
    o["a"] = a
    o["b"] = b
    return o
}

public external object C {
    val x: Int
    val y: Int
    fun f()
}