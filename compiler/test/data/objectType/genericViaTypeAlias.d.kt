package genericViaTypeAlias

external interface A<T> {
    var x: T
}
external var foo: A<String> = definedExternally
