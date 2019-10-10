import kotlin.js.*

public external abstract class A {
    open var a: Int
}

public external abstract class B : A {
    open var b: Int
}

public external abstract class C : B {
    open var c: Int
}