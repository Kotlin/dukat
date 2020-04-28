import kotlin.js.*
import org.khronos.webgl.*

public external interface A {
    var a: Int? /* = 0 */
    var b: Int? /* = 1 */
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline fun A(a: Int? = 0, b: Int? = 1): A {
    val o = js("({})")
    o["a"] = a
    o["b"] = b
    return o
}

public external interface B : A {
    var c: Int? /* = 2 */
    var d: Int? /* = 3 */
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline fun B(c: Int? = 2, d: Int? = 3, a: Int? = 0, b: Int? = 1): B {
    val o = js("({})")
    o["c"] = c
    o["d"] = d
    o["a"] = a
    o["b"] = b
    return o
}