@file:JsQualifier("path.posix")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package path.posix

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

external fun normalize(p: String): String

external fun join(vararg paths: String): String

external fun resolve(vararg pathSegments: String): String

external fun isAbsolute(p: String): Boolean

external fun relative(from: String, to: String): String

external fun dirname(p: String): String

external fun basename(p: String, ext: String? = definedExternally /* null */): String

external fun extname(p: String): String

external var sep: String

external var delimiter: String

external fun parse(p: String): path.ParsedPath

external fun format(pP: path.FormatInputPathObject): String