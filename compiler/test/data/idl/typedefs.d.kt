
external abstract class A {
    var x: Int
    var y: Int
    var intArray: Array<Int>
    fun f(x: Int): Int
}
external abstract class B : A {
    var x: A<Int>
}