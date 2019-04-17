@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
package functions

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

external fun withoutArguments(): Unit = definedExternally
external fun withOneAny(a: Any): Any = definedExternally
external fun withOneString(s: String): String = definedExternally
external fun withOneNumber(num: Number): Number = definedExternally
external fun withOneBoolean(bool: Boolean): Boolean = definedExternally
external fun withManyArguments(s: String, settings: JQueryAjaxSettings): Boolean = definedExternally
