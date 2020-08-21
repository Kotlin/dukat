@file:JsModule("path")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package path

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

external interface ParsedPath {
    var root: String
    var dir: String
    var base: String
    var ext: String
    var name: String
}

external interface FormatInputPathObject {
    var root: String?
        get() = definedExternally
        set(value) = definedExternally
    var dir: String?
        get() = definedExternally
        set(value) = definedExternally
    var base: String?
        get() = definedExternally
        set(value) = definedExternally
    var ext: String?
        get() = definedExternally
        set(value) = definedExternally
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun normalize(p: String): String

external fun join(vararg paths: String): String

external fun resolve(vararg pathSegments: String): String

external fun isAbsolute(path: String): Boolean

external fun relative(from: String, to: String): String

external fun dirname(p: String): String

external fun basename(p: String, ext: String? = definedExternally /* null */): String

external fun extname(p: String): String

external var sep: dynamic /* '\\' | '/' */

external var delimiter: dynamic /* ';' | ':' */

external fun parse(pathString: String): ParsedPath

external fun format(pathObject: FormatInputPathObject): String