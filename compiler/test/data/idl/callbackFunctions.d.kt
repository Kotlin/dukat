
external abstract class A {
    var event1: (a: A?, b: Int) -> Unit
    var event2: ((a: A?, b: Int) -> Unit)?
}