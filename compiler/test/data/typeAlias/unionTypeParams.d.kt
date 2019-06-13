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

external interface Map<K, V>
external interface List<T>
typealias Values<V> = List<V>
external var aliasUnionVar: dynamic /* List<Number> | Map<String, Values<Number>> */ = definedExternally
external fun aliasUnionFunction(a: List<String>): Unit
external fun aliasUnionFunction(a: Map<Number, Values<String>>): Unit
external var listOfUnionVar: Values<dynamic /* String | Number */> = definedExternally
external fun listOfUnionFunction(a: Values<dynamic /* Number | String */>): Unit