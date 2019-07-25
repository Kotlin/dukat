
external interface A {
    var x: Int
    fun f()
}
external abstract class B : A
external open class C : B {
    override var x: Int
    override fun f()
}
