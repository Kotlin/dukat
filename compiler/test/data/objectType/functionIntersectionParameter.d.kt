package functionIntersectionParameter

external interface FooIntersectionPart
external interface `T$0` {
    var foo: String
    var bar: Any
}
external open class FooIntersection {
    open fun bar(p: `T$0` /* `T$0` & FooIntersectionPart */): Unit = definedExternally
}
