
external abstract class UnresolvedUnions {
    var x: dynamic
    var y: dynamic
    fun f(x: dynamic): dynamic
}
external abstract class A : ABC
external abstract class B : ABC
external abstract class C : ABC, UnionCOrD
external abstract class D : ABCD, UnionCOrD
external abstract class ResolvedUnions {
    var x: ABC?
    var y: Array<ABCD>
    var z: UnionCOrD?
    var w: dynamic
    fun f(x: ABC?): ABC
}
external interface ABC : ABCD
external interface ABCD
external interface UnionCOrD
