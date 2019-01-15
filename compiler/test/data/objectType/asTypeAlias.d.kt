package asTypeAlias

external interface I {
    fun foo(): String
}
external interface J {
    fun foo(): String
}
external interface K {
    fun bar(): Number
}
external interface Q
external interface W
external fun f(a: I, b: J, c: I, q: Q, w: W): K = definedExternally
external var x: I = definedExternally
external var y: I = definedExternally
external var z: J = definedExternally
external var q: Q = definedExternally
external var w: W = definedExternally
