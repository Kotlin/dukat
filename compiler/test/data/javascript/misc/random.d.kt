@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

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

external object _ {
    fun values(obj: Any?)
    fun keys(obj: Any?): dynamic /* Any? | Unit */
    fun negate(predicate: Any?): Boolean
    fun isArray(obj: Any?): Boolean
    fun isObject(obj: Any?): Boolean
    fun isElement(obj: Any?): Boolean
    fun isEmpty(obj: Any?): Boolean
}

external fun values(obj: Any?)

external fun keys(obj: Any?): dynamic /* Any? | Unit */

external fun negate(predicate: Any?): Boolean

external fun isArray(obj: Any?): Boolean

external fun isObject(obj: Any?): Boolean

external fun isElement(obj: Any?): Boolean

external fun isEmpty(obj: Any?): Boolean

external fun keyInObj(value: Any?, key: Any?, obj: Any?): Boolean

external fun createPredicateIndexFinder(array: Any?, predicate: Any?, context: Any?, dir: Any?): Number

external fun isArrayLike(collection: Any?): Boolean