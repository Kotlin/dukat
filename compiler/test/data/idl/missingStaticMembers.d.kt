
external abstract class A {
    companion object {
        fun f()
        val x: Int
        var y: Int
    }
}
external abstract class B : A {
    companion object {
        fun f()
        val x: Int
        var y: Int
    }
}
external abstract class C : B {
    companion object {
        fun f()
        val x: Int
        var y: Int
    }
}