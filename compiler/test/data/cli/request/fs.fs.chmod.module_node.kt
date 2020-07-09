@file:JsQualifier("fs.chmod")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package fs.chmod

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

external fun __promisify__(path: String, mode: String): Promise<Unit>

external fun __promisify__(path: String, mode: Number): Promise<Unit>

external fun __promisify__(path: Buffer, mode: String): Promise<Unit>

external fun __promisify__(path: Buffer, mode: Number): Promise<Unit>

external fun __promisify__(path: URL, mode: String): Promise<Unit>

external fun __promisify__(path: URL, mode: Number): Promise<Unit>