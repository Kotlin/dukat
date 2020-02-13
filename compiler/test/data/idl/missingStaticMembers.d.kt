import kotlin.js.*
import org.khronos.webgl.*

public external abstract class A {
    companion object {
        val x: Int
        var y: Int
        fun f()
    }
}

public external abstract class B : A {
    companion object {
        val x: Int
        var y: Int
        fun f()
    }
}

public external abstract class C : B {
    companion object {
        val x: Int
        var y: Int
        fun f()
    }
}