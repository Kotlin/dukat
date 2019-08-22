import kotlin.js.*

external abstract class A {
    companion object {
        val x: Int
        var y: Int
        fun f()
    }
}

external abstract class B : A {
    companion object {
        val x: Int
        var y: Int
        fun f()
    }
}

external abstract class C : B {
    companion object {
        val x: Int
        var y: Int
        fun f()
    }
}