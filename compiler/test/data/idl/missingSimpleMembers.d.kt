import kotlin.js.*

public external interface A {
    var x: Int
    fun f()
}

public external abstract class B : A

public external open class C : B {
    override var x: Int
    override fun f()
}
