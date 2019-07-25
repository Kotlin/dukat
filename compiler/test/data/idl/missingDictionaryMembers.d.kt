
external interface A {
    var a: Int? get() = definedExternally; set(value) = definedExternally
    var b: Int? get() = definedExternally; set(value) = definedExternally
}
@kotlin.internal.InlineOnly
inline fun A(a: Int? = 0, b: Int? = 1): A { val o = js("({})"); o["a"] = a; o["b"] = b; return o }
external interface B : A {
    var c: Int? get() = definedExternally; set(value) = definedExternally
    var d: Int? get() = definedExternally; set(value) = definedExternally
}
@kotlin.internal.InlineOnly
inline fun B(c: Int? = 2, d: Int? = 3, a: Int? = 0, b: Int? = 1): B { val o = js("({})"); o["c"] = c; o["d"] = d; o["a"] = a; o["b"] = b; return o }