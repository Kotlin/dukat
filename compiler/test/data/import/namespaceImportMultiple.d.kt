@file:JsQualifier("lib1")
package namespaceImportMultiple.lib1

external interface I {
    var x: String
}

// ------------------------------------------------------------------------------------------
@file:JsQualifier("lib1.lib3")
package namespaceImportMultiple.lib1.lib3

external interface K

// ------------------------------------------------------------------------------------------
@file:JsQualifier("lib1.lib2")
package namespaceImportMultiple.lib1.lib2

external interface J {
    var y: lib1.I
}

// ------------------------------------------------------------------------------------------
@file:JsQualifier("lib1.lib2.lib3")
package namespaceImportMultiple.lib1.lib2.lib3

external fun foo(x: lib1.lib2.J, y: lib1.I, z: lib1.lib3.K, v: lib1.lib3.K): Unit = definedExternally
