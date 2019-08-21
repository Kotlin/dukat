
import kotlin.js.*

external abstract class A {
    open var a: Int
}

external abstract class B : A {
    open var b: Int
}

external abstract class C : B {
    open var c: Int
}