import kotlin.js.*

external abstract class A {
    open var s1: String
    open var s2: String
    open var s3: String
    open var s4: String
    open var s5: String
    open var nullableString: String?
    open var obj: dynamic
    open var nullableObject: dynamic
    open var value: dynamic
    open var child: A<Int>?
    open var intArray: Array<Int>
    open var intArray2D: Array<Array<Int>>
    open var intSequence: Array<Int>
    open var promise: Promise<Unit>
    open var readArray: Array<out Int>
    open var rec: dynamic
    fun f(x: Array<Int>): Array<Int>
    fun g(x: A<Int>): A<Int>
    fun h(): Any?
}