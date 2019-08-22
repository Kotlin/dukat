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

external fun withOneAny(a: Any? = definedExternally /* null */): Any

external fun withOneString(s: String? = definedExternally /* null */): String

external fun withManyArguments(s: String? = definedExternally /* null */, settings: JQueryAjaxSettings? = definedExternally /* null */): Boolean

external fun withOptionalQualified(opts: ping.Options? = definedExternally /* null */)
