@file:JsQualifier("Foo")
package withMerging.Foo

external var variableFoo: Number = definedExternally
external fun funcFoo(): Unit = definedExternally
external open class AFoo

// ------------------------------------------------------------------------------------------
@file:JsQualifier("Foo.Bar")
package withMerging.Foo.Bar

external var variableFooBar: Number = definedExternally
external fun funcFooBar(): Unit = definedExternally
external open class AFooBar

// ------------------------------------------------------------------------------------------
@file:JsQualifier("Foo.Bar.Baz")
package withMerging.Foo.Bar.Baz

external var variableFooBarBaz: Number = definedExternally
external fun funcFooBarBaz(): Unit = definedExternally
external open class AFooBarBaz
external var anotherVariableFooBarBaz: Number = definedExternally
external fun anotherFuncFooBarBaz(): Unit = definedExternally
external open class AnotherAFooBarBaz
