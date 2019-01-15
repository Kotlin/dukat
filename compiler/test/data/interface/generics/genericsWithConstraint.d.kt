external interface Foo<T : Bar> {
    var varT: T
    fun withoutArgumentsReturnsT(): T
    fun withOneT(a: T): T
    fun <B : Baz> returnsB(a: Any): B
    fun <A : T, B : B> withManyArguments(a: A, b: B): T
}
