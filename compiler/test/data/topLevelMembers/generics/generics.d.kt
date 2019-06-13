@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")

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

external fun <T> withoutArgumentsReturnsT(): T
external fun <T> withOneT(a: T): Any
external fun <T> returnsT(s: String): T
external fun <A, B> withManyArguments(a: A, b: B): Boolean
external var arrayOfAny: Array<Any>
external var arrayOfArray: Array<Array<String>>
external var arrayOfList: Array<List<String>>
external var arrayOfListBySquare: Array<List<Boolean>>
external var listOfArray: List<Array<Any>>
external var listOfArrayBySquare: List<Array<Number>>
