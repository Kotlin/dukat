import kotlin.js.*

public external interface A {
    var a: Int? /* = 0 */
        get() = definedExternally
        set(value) = definedExternally
    var b: Int? /* = 1 */
        get() = definedExternally
        set(value) = definedExternally
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
        get() = definedExternally
        set(value) = definedExternally
    var d: Int? /* = 3 */
        get() = definedExternally
        set(value) = definedExternally
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