package generics

external interface ComponentSpec<P, S>
external interface Foo<T> {
    var varT: T
    fun withoutArgumentsReturnsT(): T
    fun withOneT(a: T): T
    fun <B> returnsB(a: Any): B
    fun <A, B> withManyArguments(a: A, b: B): T
    fun <P, S> withMultipleTypeParamsInParam(spec: ComponentSpec<P, S>): String
}
