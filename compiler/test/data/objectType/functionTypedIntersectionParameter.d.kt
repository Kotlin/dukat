package functionTypedIntersectionParameter

external interface FooPart<T>
external interface `T$0`<T> {
    var foo: T
    var sup: Any
}
external interface `T$1`<T> {
    var foo: T
    var bar: Any
}
external open class FooTypedUnion {
    open fun <T> baz(p: `T$0`<T>): Unit = definedExternally
    open fun <T> bar(p: `T$1`<T> /* `T$1`<T> & FooPart<T> */): Unit = definedExternally
}
