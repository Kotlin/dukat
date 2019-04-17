@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
package functionsWithVararg

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external fun withVarargWithoutTypeAnnotation(vararg a: Any): Unit = definedExternally
external fun withVarargAny(vararg a: Any): Any = definedExternally
external fun withVarargNumber(vararg s: Number): String = definedExternally
external fun withManyArguments(n: Number, vararg s: String): Boolean = definedExternally
external fun withVarargWithGenericArrayOfNumber(vararg numbers: Number): String = definedExternally
external fun withVarargWithGenericArrayOfFoo(vararg foos: Foo): String = definedExternally
