@file:JsModule("<RESOLVED_MODULE_NAME>")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package Pako

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

external enum class FlushValues {
    Z_NO_FLUSH /* = 0 */,
    Z_PARTIAL_FLUSH /* = 1 */,
    Z_SYNC_FLUSH /* = 2 */,
    Z_FULL_FLUSH /* = 3 */,
    Z_FINISH /* = 4 */,
    Z_BLOCK /* = 5 */,
    Z_TREES /* = 6 */
}
