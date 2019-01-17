@file:JsQualifier("lib")
package importInSupertype.lib

external interface I

// ------------------------------------------------------------------------------------------
@file:JsQualifier("main")
package importInSupertype.main

external interface J : lib.I
