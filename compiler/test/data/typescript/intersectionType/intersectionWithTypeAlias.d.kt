// [test] intersectionWithTypeAlias.kt
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

import kotlin.js.*
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

external interface Coordinates {
    var longitude: Number
    var latitude: Number
}

external interface `T$0` {
    var zoom: Number
}

external interface `T$1` {
    var scale: Number
}

external fun bestCoordinates(a: Coordinates /* Coordinates & `T$0` | Coordinates & `T$1` | Coordinates & `T$1` */, b: Coordinates /* Coordinates & `T$0` | Coordinates & `T$1` | Coordinates & `T$0` | Coordinates & `T$1` */): dynamic /* Coordinates & `T$0` | Coordinates & `T$1` */

external open class WithTrickyConstuctor(scales: Coordinates /* Coordinates & `T$0` */)