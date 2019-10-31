@file:JsQualifier("inspector.NodeTracing")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package inspector.NodeTracing

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

external interface TraceConfig {
    var recordMode: String?
        get() = definedExternally
        set(value) = definedExternally
    var includedCategories: Array<String>
}

external interface StartParameterType {
    var traceConfig: TraceConfig
}

external interface GetCategoriesReturnType {
    var categories: Array<String>
}

external interface DataCollectedEventDataType {
    var value: Array<Any>
}