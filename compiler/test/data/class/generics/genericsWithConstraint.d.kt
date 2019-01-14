external open class Foo<T : Bar> {
    open var varT: T = definedExternally
    open fun withoutArgumentsReturnsT(): T = definedExternally
    open fun withOneT(a: T): T = definedExternally
    open fun <B : Baz> returnsB(a: Any): B = definedExternally
    open fun <A : T, B : B> withManyArguments(a: A, b: B): T = definedExternally
}
