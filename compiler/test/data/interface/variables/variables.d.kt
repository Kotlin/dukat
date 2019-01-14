package variables

external interface Foo {
    var varWithoutTypeAnnotation: Any
    var varAsAny: Any
    var varAsNumber: Number
    var varAsBoolean: Boolean
    var varAsString: String
}

// ------------------------------------------------------------------------------------------
@file:JsQualifier("foo")
package variables.foo

external interface Bar {
    var name: String
}
