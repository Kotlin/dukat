
external abstract class A {
    var s1: String
    var s2: String
    var s3: String
    var nullableString: String?
    var obj: dynamic
    var nullableObject: dynamic
    var child: A<Int>?
    var intArray: Array<Int>
    var intArray2D: Array<Array<Int>>
    var intSequence: Array<Int>
    fun f(x: Array<Int>): Array<Int>
    fun g(x: A<Int>): A<Int>
}