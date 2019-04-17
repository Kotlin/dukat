@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
package generics

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

external fun <T> withoutArgumentsReturnsT(): T = definedExternally
external fun <T> withOneT(a: T): Any = definedExternally
external fun <T> returnsT(s: String): T = definedExternally
external fun <A, B> withManyArguments(a: A, b: B): Boolean = definedExternally
external var arrayOfAny: Array<Any> = definedExternally
external var arrayOfArray: Array<Array<String>> = definedExternally
external var arrayOfList: Array<List<String>> = definedExternally
external var arrayOfListBySquare: Array<List<Boolean>> = definedExternally
external var listOfArray: List<Array<Any>> = definedExternally
external var listOfArrayBySquare: List<Array<Number>> = definedExternally
