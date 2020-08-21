@file:JsQualifier("fs.rename")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package fs.rename

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

external fun __promisify__(oldPath: String, newPath: String): Promise<Unit>

external fun __promisify__(oldPath: String, newPath: Buffer): Promise<Unit>

external fun __promisify__(oldPath: String, newPath: URL): Promise<Unit>

external fun __promisify__(oldPath: Buffer, newPath: String): Promise<Unit>

external fun __promisify__(oldPath: Buffer, newPath: Buffer): Promise<Unit>

external fun __promisify__(oldPath: Buffer, newPath: URL): Promise<Unit>

external fun __promisify__(oldPath: URL, newPath: String): Promise<Unit>

external fun __promisify__(oldPath: URL, newPath: Buffer): Promise<Unit>

external fun __promisify__(oldPath: URL, newPath: URL): Promise<Unit>