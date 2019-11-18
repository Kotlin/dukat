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

external interface ConfigurationBoolean {
    operator fun get(key: String): Boolean
    operator fun set(key: String, value: Boolean)
}

external interface ExtendeConfigurationBoolean {
    operator fun get(key: String): Boolean
    operator fun set(key: String, value: Boolean)
    var valid_field: Boolean
    var other_valid_field: String
}

external interface HeterogenousConfig {
    operator fun get(key: String): Any?
    operator fun set(key: String, value: Any?)
    var valid_field: Boolean
    var other_valid_field: String
}