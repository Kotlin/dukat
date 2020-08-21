@file:JsModule("fs")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package fs

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

external interface StatsBase<T> {
    fun isFile(): Boolean
    fun isDirectory(): Boolean
    fun isBlockDevice(): Boolean
    fun isCharacterDevice(): Boolean
    fun isSymbolicLink(): Boolean
    fun isFIFO(): Boolean
    fun isSocket(): Boolean
    var dev: Number
    var ino: Number
    var mode: Number
    var nlink: Number
    var uid: Number
    var gid: Number
    var rdev: Number
    var size: Number
    var blksize: Number
    var blocks: Number
    var atimeMs: Number
    var mtimeMs: Number
    var ctimeMs: Number
    var birthtimeMs: Number
    var atime: Date
    var mtime: Date
    var ctime: Date
    var birthtime: Date
}

external open class Stats : StatsBase<Number>

external open class Dirent {
    open fun isFile(): Boolean
    open fun isDirectory(): Boolean
    open fun isBlockDevice(): Boolean
    open fun isCharacterDevice(): Boolean
    open fun isSymbolicLink(): Boolean
    open fun isFIFO(): Boolean
    open fun isSocket(): Boolean
    open var name: String
}

external interface FSWatcher : events.EventEmitter {
    fun close()
    fun addListener(event: String, listener: (args: Array<Any>) -> Unit): FSWatcher /* this */
    fun addListener(event: String /* "change" */, listener: (eventType: String, filename: dynamic /* String | Buffer */) -> Unit): FSWatcher /* this */
    fun addListener(event: String /* "error" */, listener: (error: Error) -> Unit): FSWatcher /* this */
    fun on(event: String, listener: (args: Array<Any>) -> Unit): FSWatcher /* this */
    fun on(event: String /* "change" */, listener: (eventType: String, filename: dynamic /* String | Buffer */) -> Unit): FSWatcher /* this */
    fun on(event: String /* "error" */, listener: (error: Error) -> Unit): FSWatcher /* this */
    fun once(event: String, listener: (args: Array<Any>) -> Unit): FSWatcher /* this */
    fun once(event: String /* "change" */, listener: (eventType: String, filename: dynamic /* String | Buffer */) -> Unit): FSWatcher /* this */
    fun once(event: String /* "error" */, listener: (error: Error) -> Unit): FSWatcher /* this */
    fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): FSWatcher /* this */
    fun prependListener(event: String /* "change" */, listener: (eventType: String, filename: dynamic /* String | Buffer */) -> Unit): FSWatcher /* this */
    fun prependListener(event: String /* "error" */, listener: (error: Error) -> Unit): FSWatcher /* this */
    fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): FSWatcher /* this */
    fun prependOnceListener(event: String /* "change" */, listener: (eventType: String, filename: dynamic /* String | Buffer */) -> Unit): FSWatcher /* this */
    fun prependOnceListener(event: String /* "error" */, listener: (error: Error) -> Unit): FSWatcher /* this */
}

external open class ReadStream : stream.Readable {
    open fun close()
    open var bytesRead: Number
    open var path: dynamic /* String | Buffer */
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): ReadStream /* this */
    open fun addListener(event: String /* "open" */, listener: (fd: Number) -> Unit): ReadStream /* this */
    open fun addListener(event: String /* "close" */, listener: () -> Unit): ReadStream /* this */
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): ReadStream /* this */
    open fun on(event: String /* "open" */, listener: (fd: Number) -> Unit): ReadStream /* this */
    open fun on(event: String /* "close" */, listener: () -> Unit): ReadStream /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): ReadStream /* this */
    open fun once(event: String /* "open" */, listener: (fd: Number) -> Unit): ReadStream /* this */
    open fun once(event: String /* "close" */, listener: () -> Unit): ReadStream /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): ReadStream /* this */
    open fun prependListener(event: String /* "open" */, listener: (fd: Number) -> Unit): ReadStream /* this */
    open fun prependListener(event: String /* "close" */, listener: () -> Unit): ReadStream /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): ReadStream /* this */
    open fun prependOnceListener(event: String /* "open" */, listener: (fd: Number) -> Unit): ReadStream /* this */
    open fun prependOnceListener(event: String /* "close" */, listener: () -> Unit): ReadStream /* this */
}

external open class WriteStream : stream.Writable {
    open fun close()
    open var bytesWritten: Number
    open var path: dynamic /* String | Buffer */
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): WriteStream /* this */
    open fun addListener(event: String /* "open" */, listener: (fd: Number) -> Unit): WriteStream /* this */
    open fun addListener(event: String /* "close" */, listener: () -> Unit): WriteStream /* this */
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): WriteStream /* this */
    open fun on(event: String /* "open" */, listener: (fd: Number) -> Unit): WriteStream /* this */
    open fun on(event: String /* "close" */, listener: () -> Unit): WriteStream /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): WriteStream /* this */
    open fun once(event: String /* "open" */, listener: (fd: Number) -> Unit): WriteStream /* this */
    open fun once(event: String /* "close" */, listener: () -> Unit): WriteStream /* this */
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): WriteStream /* this */
    open fun prependListener(event: String /* "open" */, listener: (fd: Number) -> Unit): WriteStream /* this */
    open fun prependListener(event: String /* "close" */, listener: () -> Unit): WriteStream /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): WriteStream /* this */
    open fun prependOnceListener(event: String /* "open" */, listener: (fd: Number) -> Unit): WriteStream /* this */
    open fun prependOnceListener(event: String /* "close" */, listener: () -> Unit): WriteStream /* this */
}

external fun rename(oldPath: String, newPath: String, callback: NoParamCallback)

external fun rename(oldPath: String, newPath: Buffer, callback: NoParamCallback)

external fun rename(oldPath: String, newPath: URL, callback: NoParamCallback)

external fun rename(oldPath: Buffer, newPath: String, callback: NoParamCallback)

external fun rename(oldPath: Buffer, newPath: Buffer, callback: NoParamCallback)

external fun rename(oldPath: Buffer, newPath: URL, callback: NoParamCallback)

external fun rename(oldPath: URL, newPath: String, callback: NoParamCallback)

external fun rename(oldPath: URL, newPath: Buffer, callback: NoParamCallback)

external fun rename(oldPath: URL, newPath: URL, callback: NoParamCallback)

external fun renameSync(oldPath: String, newPath: String)

external fun renameSync(oldPath: String, newPath: Buffer)

external fun renameSync(oldPath: String, newPath: URL)

external fun renameSync(oldPath: Buffer, newPath: String)

external fun renameSync(oldPath: Buffer, newPath: Buffer)

external fun renameSync(oldPath: Buffer, newPath: URL)

external fun renameSync(oldPath: URL, newPath: String)

external fun renameSync(oldPath: URL, newPath: Buffer)

external fun renameSync(oldPath: URL, newPath: URL)

external fun truncate(path: String, len: Number?, callback: NoParamCallback)

external fun truncate(path: Buffer, len: Number?, callback: NoParamCallback)

external fun truncate(path: URL, len: Number?, callback: NoParamCallback)

external fun truncate(path: String, callback: NoParamCallback)

external fun truncate(path: Buffer, callback: NoParamCallback)

external fun truncate(path: URL, callback: NoParamCallback)

external fun truncateSync(path: String, len: Number? = definedExternally /* null */)

external fun truncateSync(path: Buffer, len: Number? = definedExternally /* null */)

external fun truncateSync(path: URL, len: Number? = definedExternally /* null */)

external fun ftruncate(fd: Number, len: Number?, callback: NoParamCallback)

external fun ftruncate(fd: Number, callback: NoParamCallback)

external fun ftruncateSync(fd: Number, len: Number? = definedExternally /* null */)

external fun chown(path: String, uid: Number, gid: Number, callback: NoParamCallback)

external fun chown(path: Buffer, uid: Number, gid: Number, callback: NoParamCallback)

external fun chown(path: URL, uid: Number, gid: Number, callback: NoParamCallback)

external fun chownSync(path: String, uid: Number, gid: Number)

external fun chownSync(path: Buffer, uid: Number, gid: Number)

external fun chownSync(path: URL, uid: Number, gid: Number)

external fun fchown(fd: Number, uid: Number, gid: Number, callback: NoParamCallback)

external fun fchownSync(fd: Number, uid: Number, gid: Number)

external fun lchown(path: String, uid: Number, gid: Number, callback: NoParamCallback)

external fun lchown(path: Buffer, uid: Number, gid: Number, callback: NoParamCallback)

external fun lchown(path: URL, uid: Number, gid: Number, callback: NoParamCallback)

external fun lchownSync(path: String, uid: Number, gid: Number)

external fun lchownSync(path: Buffer, uid: Number, gid: Number)

external fun lchownSync(path: URL, uid: Number, gid: Number)

external fun chmod(path: String, mode: String, callback: NoParamCallback)

external fun chmod(path: String, mode: Number, callback: NoParamCallback)

external fun chmod(path: Buffer, mode: String, callback: NoParamCallback)

external fun chmod(path: Buffer, mode: Number, callback: NoParamCallback)

external fun chmod(path: URL, mode: String, callback: NoParamCallback)

external fun chmod(path: URL, mode: Number, callback: NoParamCallback)

external fun chmodSync(path: String, mode: String)

external fun chmodSync(path: String, mode: Number)

external fun chmodSync(path: Buffer, mode: String)

external fun chmodSync(path: Buffer, mode: Number)

external fun chmodSync(path: URL, mode: String)

external fun chmodSync(path: URL, mode: Number)

external fun fchmod(fd: Number, mode: String, callback: NoParamCallback)

external fun fchmod(fd: Number, mode: Number, callback: NoParamCallback)

external fun fchmodSync(fd: Number, mode: String)

external fun fchmodSync(fd: Number, mode: Number)

external fun lchmod(path: String, mode: String, callback: NoParamCallback)

external fun lchmod(path: String, mode: Number, callback: NoParamCallback)

external fun lchmod(path: Buffer, mode: String, callback: NoParamCallback)

external fun lchmod(path: Buffer, mode: Number, callback: NoParamCallback)

external fun lchmod(path: URL, mode: String, callback: NoParamCallback)

external fun lchmod(path: URL, mode: Number, callback: NoParamCallback)

external fun lchmodSync(path: String, mode: String)

external fun lchmodSync(path: String, mode: Number)

external fun lchmodSync(path: Buffer, mode: String)

external fun lchmodSync(path: Buffer, mode: Number)

external fun lchmodSync(path: URL, mode: String)

external fun lchmodSync(path: URL, mode: Number)

external fun stat(path: String, callback: (err: NodeJS.ErrnoException?, stats: Stats) -> Unit)

external fun stat(path: Buffer, callback: (err: NodeJS.ErrnoException?, stats: Stats) -> Unit)

external fun stat(path: URL, callback: (err: NodeJS.ErrnoException?, stats: Stats) -> Unit)

external fun statSync(path: String): Stats

external fun statSync(path: Buffer): Stats

external fun statSync(path: URL): Stats

external fun fstat(fd: Number, callback: (err: NodeJS.ErrnoException?, stats: Stats) -> Unit)

external fun fstatSync(fd: Number): Stats

external fun lstat(path: String, callback: (err: NodeJS.ErrnoException?, stats: Stats) -> Unit)

external fun lstat(path: Buffer, callback: (err: NodeJS.ErrnoException?, stats: Stats) -> Unit)

external fun lstat(path: URL, callback: (err: NodeJS.ErrnoException?, stats: Stats) -> Unit)

external fun lstatSync(path: String): Stats

external fun lstatSync(path: Buffer): Stats

external fun lstatSync(path: URL): Stats

external fun link(existingPath: String, newPath: String, callback: NoParamCallback)

external fun link(existingPath: String, newPath: Buffer, callback: NoParamCallback)

external fun link(existingPath: String, newPath: URL, callback: NoParamCallback)

external fun link(existingPath: Buffer, newPath: String, callback: NoParamCallback)

external fun link(existingPath: Buffer, newPath: Buffer, callback: NoParamCallback)

external fun link(existingPath: Buffer, newPath: URL, callback: NoParamCallback)

external fun link(existingPath: URL, newPath: String, callback: NoParamCallback)

external fun link(existingPath: URL, newPath: Buffer, callback: NoParamCallback)

external fun link(existingPath: URL, newPath: URL, callback: NoParamCallback)

external fun linkSync(existingPath: String, newPath: String)

external fun linkSync(existingPath: String, newPath: Buffer)

external fun linkSync(existingPath: String, newPath: URL)

external fun linkSync(existingPath: Buffer, newPath: String)

external fun linkSync(existingPath: Buffer, newPath: Buffer)

external fun linkSync(existingPath: Buffer, newPath: URL)

external fun linkSync(existingPath: URL, newPath: String)

external fun linkSync(existingPath: URL, newPath: Buffer)

external fun linkSync(existingPath: URL, newPath: URL)

external fun symlink(target: String, path: String, type: dynamic /* "dir" | "file" | "junction" */, callback: NoParamCallback)

external fun symlink(target: String, path: Buffer, type: dynamic /* "dir" | "file" | "junction" */, callback: NoParamCallback)

external fun symlink(target: String, path: URL, type: dynamic /* "dir" | "file" | "junction" */, callback: NoParamCallback)

external fun symlink(target: Buffer, path: String, type: dynamic /* "dir" | "file" | "junction" */, callback: NoParamCallback)

external fun symlink(target: Buffer, path: Buffer, type: dynamic /* "dir" | "file" | "junction" */, callback: NoParamCallback)

external fun symlink(target: Buffer, path: URL, type: dynamic /* "dir" | "file" | "junction" */, callback: NoParamCallback)

external fun symlink(target: URL, path: String, type: dynamic /* "dir" | "file" | "junction" */, callback: NoParamCallback)

external fun symlink(target: URL, path: Buffer, type: dynamic /* "dir" | "file" | "junction" */, callback: NoParamCallback)

external fun symlink(target: URL, path: URL, type: dynamic /* "dir" | "file" | "junction" */, callback: NoParamCallback)

external fun symlink(target: String, path: String, callback: NoParamCallback)

external fun symlink(target: String, path: Buffer, callback: NoParamCallback)

external fun symlink(target: String, path: URL, callback: NoParamCallback)

external fun symlink(target: Buffer, path: String, callback: NoParamCallback)

external fun symlink(target: Buffer, path: Buffer, callback: NoParamCallback)

external fun symlink(target: Buffer, path: URL, callback: NoParamCallback)

external fun symlink(target: URL, path: String, callback: NoParamCallback)

external fun symlink(target: URL, path: Buffer, callback: NoParamCallback)

external fun symlink(target: URL, path: URL, callback: NoParamCallback)

external fun symlinkSync(target: String, path: String, type: dynamic /* "dir" | "file" | "junction" */ = definedExternally /* null */)

external fun symlinkSync(target: String, path: Buffer, type: dynamic /* "dir" | "file" | "junction" */ = definedExternally /* null */)

external fun symlinkSync(target: String, path: URL, type: dynamic /* "dir" | "file" | "junction" */ = definedExternally /* null */)

external fun symlinkSync(target: Buffer, path: String, type: dynamic /* "dir" | "file" | "junction" */ = definedExternally /* null */)

external fun symlinkSync(target: Buffer, path: Buffer, type: dynamic /* "dir" | "file" | "junction" */ = definedExternally /* null */)

external fun symlinkSync(target: Buffer, path: URL, type: dynamic /* "dir" | "file" | "junction" */ = definedExternally /* null */)

external fun symlinkSync(target: URL, path: String, type: dynamic /* "dir" | "file" | "junction" */ = definedExternally /* null */)

external fun symlinkSync(target: URL, path: Buffer, type: dynamic /* "dir" | "file" | "junction" */ = definedExternally /* null */)

external fun symlinkSync(target: URL, path: URL, type: dynamic /* "dir" | "file" | "junction" */ = definedExternally /* null */)

external interface `T$0` {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
        get() = definedExternally
        set(value) = definedExternally
}

external fun readlink(path: String, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, linkString: String) -> Unit)

external fun readlink(path: Buffer, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, linkString: String) -> Unit)

external fun readlink(path: URL, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, linkString: String) -> Unit)

external interface `T$1` {
    var encoding: String /* "buffer" */
}

external fun readlink(path: String, options: `T$1`, callback: (err: NodeJS.ErrnoException?, linkString: Buffer) -> Unit)

external fun readlink(path: String, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, linkString: Buffer) -> Unit)

external fun readlink(path: Buffer, options: `T$1`, callback: (err: NodeJS.ErrnoException?, linkString: Buffer) -> Unit)

external fun readlink(path: Buffer, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, linkString: Buffer) -> Unit)

external fun readlink(path: URL, options: `T$1`, callback: (err: NodeJS.ErrnoException?, linkString: Buffer) -> Unit)

external fun readlink(path: URL, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, linkString: Buffer) -> Unit)

external interface `T$2` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun readlink(path: String, options: `T$2`, callback: (err: NodeJS.ErrnoException?, linkString: dynamic /* String | Buffer */) -> Unit)

external fun readlink(path: String, options: String, callback: (err: NodeJS.ErrnoException?, linkString: dynamic /* String | Buffer */) -> Unit)

external fun readlink(path: String, options: Nothing?, callback: (err: NodeJS.ErrnoException?, linkString: dynamic /* String | Buffer */) -> Unit)

external fun readlink(path: Buffer, options: `T$2`, callback: (err: NodeJS.ErrnoException?, linkString: dynamic /* String | Buffer */) -> Unit)

external fun readlink(path: Buffer, options: String, callback: (err: NodeJS.ErrnoException?, linkString: dynamic /* String | Buffer */) -> Unit)

external fun readlink(path: Buffer, options: Nothing?, callback: (err: NodeJS.ErrnoException?, linkString: dynamic /* String | Buffer */) -> Unit)

external fun readlink(path: URL, options: `T$2`, callback: (err: NodeJS.ErrnoException?, linkString: dynamic /* String | Buffer */) -> Unit)

external fun readlink(path: URL, options: String, callback: (err: NodeJS.ErrnoException?, linkString: dynamic /* String | Buffer */) -> Unit)

external fun readlink(path: URL, options: Nothing?, callback: (err: NodeJS.ErrnoException?, linkString: dynamic /* String | Buffer */) -> Unit)

external fun readlink(path: String, callback: (err: NodeJS.ErrnoException?, linkString: String) -> Unit)

external fun readlink(path: Buffer, callback: (err: NodeJS.ErrnoException?, linkString: String) -> Unit)

external fun readlink(path: URL, callback: (err: NodeJS.ErrnoException?, linkString: String) -> Unit)

external fun readlinkSync(path: String, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): String

external fun readlinkSync(path: Buffer, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): String

external fun readlinkSync(path: URL, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): String

external fun readlinkSync(path: String, options: `T$1`): Buffer

external fun readlinkSync(path: String, options: String /* "buffer" */): Buffer

external fun readlinkSync(path: Buffer, options: `T$1`): Buffer

external fun readlinkSync(path: Buffer, options: String /* "buffer" */): Buffer

external fun readlinkSync(path: URL, options: `T$1`): Buffer

external fun readlinkSync(path: URL, options: String /* "buffer" */): Buffer

external fun readlinkSync(path: String, options: `T$2`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readlinkSync(path: String, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readlinkSync(path: String, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readlinkSync(path: Buffer, options: `T$2`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readlinkSync(path: Buffer, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readlinkSync(path: Buffer, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readlinkSync(path: URL, options: `T$2`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readlinkSync(path: URL, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readlinkSync(path: URL, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun realpath(path: String, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun realpath(path: Buffer, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun realpath(path: URL, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun realpath(path: String, options: `T$1`, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun realpath(path: String, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun realpath(path: Buffer, options: `T$1`, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun realpath(path: Buffer, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun realpath(path: URL, options: `T$1`, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun realpath(path: URL, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, resolvedPath: Buffer) -> Unit)

external fun realpath(path: String, options: `T$2`, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun realpath(path: String, options: String, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun realpath(path: String, options: Nothing?, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun realpath(path: Buffer, options: `T$2`, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun realpath(path: Buffer, options: String, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun realpath(path: Buffer, options: Nothing?, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun realpath(path: URL, options: `T$2`, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun realpath(path: URL, options: String, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun realpath(path: URL, options: Nothing?, callback: (err: NodeJS.ErrnoException?, resolvedPath: dynamic /* String | Buffer */) -> Unit)

external fun realpath(path: String, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun realpath(path: Buffer, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun realpath(path: URL, callback: (err: NodeJS.ErrnoException?, resolvedPath: String) -> Unit)

external fun realpathSync(path: String, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): String

external fun realpathSync(path: Buffer, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): String

external fun realpathSync(path: URL, options: dynamic /* `T$0` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): String

external fun realpathSync(path: String, options: `T$1`): Buffer

external fun realpathSync(path: String, options: String /* "buffer" */): Buffer

external fun realpathSync(path: Buffer, options: `T$1`): Buffer

external fun realpathSync(path: Buffer, options: String /* "buffer" */): Buffer

external fun realpathSync(path: URL, options: `T$1`): Buffer

external fun realpathSync(path: URL, options: String /* "buffer" */): Buffer

external fun realpathSync(path: String, options: `T$2`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun realpathSync(path: String, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun realpathSync(path: String, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun realpathSync(path: Buffer, options: `T$2`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun realpathSync(path: Buffer, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun realpathSync(path: Buffer, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun realpathSync(path: URL, options: `T$2`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun realpathSync(path: URL, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun realpathSync(path: URL, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun unlink(path: String, callback: NoParamCallback)

external fun unlink(path: Buffer, callback: NoParamCallback)

external fun unlink(path: URL, callback: NoParamCallback)

external fun unlinkSync(path: String)

external fun unlinkSync(path: Buffer)

external fun unlinkSync(path: URL)

external interface RmDirOptions {
    var recursive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RmDirAsyncOptions : RmDirOptions {
    var emfileWait: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxBusyTries: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external fun rmdir(path: String, callback: NoParamCallback)

external fun rmdir(path: Buffer, callback: NoParamCallback)

external fun rmdir(path: URL, callback: NoParamCallback)

external fun rmdir(path: String, options: RmDirAsyncOptions, callback: NoParamCallback)

external fun rmdir(path: Buffer, options: RmDirAsyncOptions, callback: NoParamCallback)

external fun rmdir(path: URL, options: RmDirAsyncOptions, callback: NoParamCallback)

external fun rmdirSync(path: String, options: RmDirOptions? = definedExternally /* null */)

external fun rmdirSync(path: Buffer, options: RmDirOptions? = definedExternally /* null */)

external fun rmdirSync(path: URL, options: RmDirOptions? = definedExternally /* null */)

external interface MakeDirectoryOptions {
    var recursive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var mode: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external fun mkdir(path: String, options: Number, callback: NoParamCallback)

external fun mkdir(path: String, options: String, callback: NoParamCallback)

external fun mkdir(path: String, options: MakeDirectoryOptions, callback: NoParamCallback)

external fun mkdir(path: String, options: Nothing?, callback: NoParamCallback)

external fun mkdir(path: Buffer, options: Number, callback: NoParamCallback)

external fun mkdir(path: Buffer, options: String, callback: NoParamCallback)

external fun mkdir(path: Buffer, options: MakeDirectoryOptions, callback: NoParamCallback)

external fun mkdir(path: Buffer, options: Nothing?, callback: NoParamCallback)

external fun mkdir(path: URL, options: Number, callback: NoParamCallback)

external fun mkdir(path: URL, options: String, callback: NoParamCallback)

external fun mkdir(path: URL, options: MakeDirectoryOptions, callback: NoParamCallback)

external fun mkdir(path: URL, options: Nothing?, callback: NoParamCallback)

external fun mkdir(path: String, callback: NoParamCallback)

external fun mkdir(path: Buffer, callback: NoParamCallback)

external fun mkdir(path: URL, callback: NoParamCallback)

external fun mkdirSync(path: String, options: Number? = definedExternally /* null */)

external fun mkdirSync(path: String, options: String? = definedExternally /* null */)

external fun mkdirSync(path: String, options: MakeDirectoryOptions? = definedExternally /* null */)

external fun mkdirSync(path: String, options: Nothing? = definedExternally /* null */)

external fun mkdirSync(path: Buffer, options: Number? = definedExternally /* null */)

external fun mkdirSync(path: Buffer, options: String? = definedExternally /* null */)

external fun mkdirSync(path: Buffer, options: MakeDirectoryOptions? = definedExternally /* null */)

external fun mkdirSync(path: Buffer, options: Nothing? = definedExternally /* null */)

external fun mkdirSync(path: URL, options: Number? = definedExternally /* null */)

external fun mkdirSync(path: URL, options: String? = definedExternally /* null */)

external fun mkdirSync(path: URL, options: MakeDirectoryOptions? = definedExternally /* null */)

external fun mkdirSync(path: URL, options: Nothing? = definedExternally /* null */)

external fun mkdtemp(prefix: String, options: `T$0`, callback: (err: NodeJS.ErrnoException?, folder: String) -> Unit)

external fun mkdtemp(prefix: String, options: String /* "ascii" */, callback: (err: NodeJS.ErrnoException?, folder: String) -> Unit)

external fun mkdtemp(prefix: String, options: Nothing?, callback: (err: NodeJS.ErrnoException?, folder: String) -> Unit)

external fun mkdtemp(prefix: String, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, folder: Buffer) -> Unit)

external fun mkdtemp(prefix: String, options: `T$1`, callback: (err: NodeJS.ErrnoException?, folder: Buffer) -> Unit)

external fun mkdtemp(prefix: String, options: `T$2`, callback: (err: NodeJS.ErrnoException?, folder: dynamic /* String | Buffer */) -> Unit)

external fun mkdtemp(prefix: String, options: String, callback: (err: NodeJS.ErrnoException?, folder: dynamic /* String | Buffer */) -> Unit)

external fun mkdtemp(prefix: String, options: Nothing?, callback: (err: NodeJS.ErrnoException?, folder: dynamic /* String | Buffer */) -> Unit)

external fun mkdtemp(prefix: String, callback: (err: NodeJS.ErrnoException?, folder: String) -> Unit)

external fun mkdtempSync(prefix: String, options: `T$0`? = definedExternally /* null */): String

external fun mkdtempSync(prefix: String, options: String /* "ascii" */ = definedExternally /* null */): String

external fun mkdtempSync(prefix: String, options: Nothing? = definedExternally /* null */): String

external fun mkdtempSync(prefix: String, options: `T$1`): Buffer

external fun mkdtempSync(prefix: String, options: String /* "buffer" */): Buffer

external fun mkdtempSync(prefix: String, options: `T$2`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun mkdtempSync(prefix: String, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun mkdtempSync(prefix: String, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external interface `T$15` {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
    var withFileTypes: String /* false */
}

external fun readdir(path: String, options: dynamic /* `T$15` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, files: Array<String>) -> Unit)

external fun readdir(path: Buffer, options: dynamic /* `T$15` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, files: Array<String>) -> Unit)

external fun readdir(path: URL, options: dynamic /* `T$15` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, files: Array<String>) -> Unit)

external interface `T$16` {
    var encoding: String /* "buffer" */
    var withFileTypes: String /* false */
}

external fun readdir(path: String, options: `T$16`, callback: (err: NodeJS.ErrnoException?, files: Array<Buffer>) -> Unit)

external fun readdir(path: String, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, files: Array<Buffer>) -> Unit)

external fun readdir(path: Buffer, options: `T$16`, callback: (err: NodeJS.ErrnoException?, files: Array<Buffer>) -> Unit)

external fun readdir(path: Buffer, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, files: Array<Buffer>) -> Unit)

external fun readdir(path: URL, options: `T$16`, callback: (err: NodeJS.ErrnoException?, files: Array<Buffer>) -> Unit)

external fun readdir(path: URL, options: String /* "buffer" */, callback: (err: NodeJS.ErrnoException?, files: Array<Buffer>) -> Unit)

external interface `T$17` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var withFileTypes: String /* false */
}

external fun readdir(path: String, options: `T$17`, callback: (err: NodeJS.ErrnoException?, files: dynamic /* Array<String> | Array<Buffer> */) -> Unit)

external fun readdir(path: String, options: String, callback: (err: NodeJS.ErrnoException?, files: dynamic /* Array<String> | Array<Buffer> */) -> Unit)

external fun readdir(path: String, options: Nothing?, callback: (err: NodeJS.ErrnoException?, files: dynamic /* Array<String> | Array<Buffer> */) -> Unit)

external fun readdir(path: Buffer, options: `T$17`, callback: (err: NodeJS.ErrnoException?, files: dynamic /* Array<String> | Array<Buffer> */) -> Unit)

external fun readdir(path: Buffer, options: String, callback: (err: NodeJS.ErrnoException?, files: dynamic /* Array<String> | Array<Buffer> */) -> Unit)

external fun readdir(path: Buffer, options: Nothing?, callback: (err: NodeJS.ErrnoException?, files: dynamic /* Array<String> | Array<Buffer> */) -> Unit)

external fun readdir(path: URL, options: `T$17`, callback: (err: NodeJS.ErrnoException?, files: dynamic /* Array<String> | Array<Buffer> */) -> Unit)

external fun readdir(path: URL, options: String, callback: (err: NodeJS.ErrnoException?, files: dynamic /* Array<String> | Array<Buffer> */) -> Unit)

external fun readdir(path: URL, options: Nothing?, callback: (err: NodeJS.ErrnoException?, files: dynamic /* Array<String> | Array<Buffer> */) -> Unit)

external fun readdir(path: String, callback: (err: NodeJS.ErrnoException?, files: Array<String>) -> Unit)

external fun readdir(path: Buffer, callback: (err: NodeJS.ErrnoException?, files: Array<String>) -> Unit)

external fun readdir(path: URL, callback: (err: NodeJS.ErrnoException?, files: Array<String>) -> Unit)

external interface `T$18` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var withFileTypes: String /* true */
}

external fun readdir(path: String, options: `T$18`, callback: (err: NodeJS.ErrnoException?, files: Array<Dirent>) -> Unit)

external fun readdir(path: Buffer, options: `T$18`, callback: (err: NodeJS.ErrnoException?, files: Array<Dirent>) -> Unit)

external fun readdir(path: URL, options: `T$18`, callback: (err: NodeJS.ErrnoException?, files: Array<Dirent>) -> Unit)

external fun readdirSync(path: String, options: dynamic /* `T$15` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): Array<String>

external fun readdirSync(path: Buffer, options: dynamic /* `T$15` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): Array<String>

external fun readdirSync(path: URL, options: dynamic /* `T$15` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? */ = definedExternally /* null */): Array<String>

external fun readdirSync(path: String, options: `T$16`): Array<Buffer>

external fun readdirSync(path: String, options: String /* "buffer" */): Array<Buffer>

external fun readdirSync(path: Buffer, options: `T$16`): Array<Buffer>

external fun readdirSync(path: Buffer, options: String /* "buffer" */): Array<Buffer>

external fun readdirSync(path: URL, options: `T$16`): Array<Buffer>

external fun readdirSync(path: URL, options: String /* "buffer" */): Array<Buffer>

external fun readdirSync(path: String, options: `T$17`? = definedExternally /* null */): dynamic /* Array<String> | Array<Buffer> */

external fun readdirSync(path: String, options: String? = definedExternally /* null */): dynamic /* Array<String> | Array<Buffer> */

external fun readdirSync(path: String, options: Nothing? = definedExternally /* null */): dynamic /* Array<String> | Array<Buffer> */

external fun readdirSync(path: Buffer, options: `T$17`? = definedExternally /* null */): dynamic /* Array<String> | Array<Buffer> */

external fun readdirSync(path: Buffer, options: String? = definedExternally /* null */): dynamic /* Array<String> | Array<Buffer> */

external fun readdirSync(path: Buffer, options: Nothing? = definedExternally /* null */): dynamic /* Array<String> | Array<Buffer> */

external fun readdirSync(path: URL, options: `T$17`? = definedExternally /* null */): dynamic /* Array<String> | Array<Buffer> */

external fun readdirSync(path: URL, options: String? = definedExternally /* null */): dynamic /* Array<String> | Array<Buffer> */

external fun readdirSync(path: URL, options: Nothing? = definedExternally /* null */): dynamic /* Array<String> | Array<Buffer> */

external fun readdirSync(path: String, options: `T$18`): Array<Dirent>

external fun readdirSync(path: Buffer, options: `T$18`): Array<Dirent>

external fun readdirSync(path: URL, options: `T$18`): Array<Dirent>

external fun close(fd: Number, callback: NoParamCallback)

external fun closeSync(fd: Number)

external fun open(path: String, flags: String, mode: dynamic /* String | Number | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun open(path: String, flags: Number, mode: dynamic /* String | Number | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun open(path: Buffer, flags: String, mode: dynamic /* String | Number | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun open(path: Buffer, flags: Number, mode: dynamic /* String | Number | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun open(path: URL, flags: String, mode: dynamic /* String | Number | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun open(path: URL, flags: Number, mode: dynamic /* String | Number | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun open(path: String, flags: String, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun open(path: String, flags: Number, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun open(path: Buffer, flags: String, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun open(path: Buffer, flags: Number, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun open(path: URL, flags: String, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun open(path: URL, flags: Number, callback: (err: NodeJS.ErrnoException?, fd: Number) -> Unit)

external fun openSync(path: String, flags: String, mode: dynamic /* String | Number | Nothing? */ = definedExternally /* null */): Number

external fun openSync(path: String, flags: Number, mode: dynamic /* String | Number | Nothing? */ = definedExternally /* null */): Number

external fun openSync(path: Buffer, flags: String, mode: dynamic /* String | Number | Nothing? */ = definedExternally /* null */): Number

external fun openSync(path: Buffer, flags: Number, mode: dynamic /* String | Number | Nothing? */ = definedExternally /* null */): Number

external fun openSync(path: URL, flags: String, mode: dynamic /* String | Number | Nothing? */ = definedExternally /* null */): Number

external fun openSync(path: URL, flags: Number, mode: dynamic /* String | Number | Nothing? */ = definedExternally /* null */): Number

external fun utimes(path: String, atime: String, mtime: dynamic /* String | Number | Date */, callback: NoParamCallback)

external fun utimes(path: String, atime: Number, mtime: dynamic /* String | Number | Date */, callback: NoParamCallback)

external fun utimes(path: String, atime: Date, mtime: dynamic /* String | Number | Date */, callback: NoParamCallback)

external fun utimes(path: Buffer, atime: String, mtime: dynamic /* String | Number | Date */, callback: NoParamCallback)

external fun utimes(path: Buffer, atime: Number, mtime: dynamic /* String | Number | Date */, callback: NoParamCallback)

external fun utimes(path: Buffer, atime: Date, mtime: dynamic /* String | Number | Date */, callback: NoParamCallback)

external fun utimes(path: URL, atime: String, mtime: dynamic /* String | Number | Date */, callback: NoParamCallback)

external fun utimes(path: URL, atime: Number, mtime: dynamic /* String | Number | Date */, callback: NoParamCallback)

external fun utimes(path: URL, atime: Date, mtime: dynamic /* String | Number | Date */, callback: NoParamCallback)

external fun utimesSync(path: String, atime: String, mtime: dynamic /* String | Number | Date */)

external fun utimesSync(path: String, atime: Number, mtime: dynamic /* String | Number | Date */)

external fun utimesSync(path: String, atime: Date, mtime: dynamic /* String | Number | Date */)

external fun utimesSync(path: Buffer, atime: String, mtime: dynamic /* String | Number | Date */)

external fun utimesSync(path: Buffer, atime: Number, mtime: dynamic /* String | Number | Date */)

external fun utimesSync(path: Buffer, atime: Date, mtime: dynamic /* String | Number | Date */)

external fun utimesSync(path: URL, atime: String, mtime: dynamic /* String | Number | Date */)

external fun utimesSync(path: URL, atime: Number, mtime: dynamic /* String | Number | Date */)

external fun utimesSync(path: URL, atime: Date, mtime: dynamic /* String | Number | Date */)

external fun futimes(fd: Number, atime: String, mtime: String, callback: NoParamCallback)

external fun futimes(fd: Number, atime: String, mtime: Number, callback: NoParamCallback)

external fun futimes(fd: Number, atime: String, mtime: Date, callback: NoParamCallback)

external fun futimes(fd: Number, atime: Number, mtime: String, callback: NoParamCallback)

external fun futimes(fd: Number, atime: Number, mtime: Number, callback: NoParamCallback)

external fun futimes(fd: Number, atime: Number, mtime: Date, callback: NoParamCallback)

external fun futimes(fd: Number, atime: Date, mtime: String, callback: NoParamCallback)

external fun futimes(fd: Number, atime: Date, mtime: Number, callback: NoParamCallback)

external fun futimes(fd: Number, atime: Date, mtime: Date, callback: NoParamCallback)

external fun futimesSync(fd: Number, atime: String, mtime: String)

external fun futimesSync(fd: Number, atime: String, mtime: Number)

external fun futimesSync(fd: Number, atime: String, mtime: Date)

external fun futimesSync(fd: Number, atime: Number, mtime: String)

external fun futimesSync(fd: Number, atime: Number, mtime: Number)

external fun futimesSync(fd: Number, atime: Number, mtime: Date)

external fun futimesSync(fd: Number, atime: Date, mtime: String)

external fun futimesSync(fd: Number, atime: Date, mtime: Number)

external fun futimesSync(fd: Number, atime: Date, mtime: Date)

external fun fsync(fd: Number, callback: NoParamCallback)

external fun fsyncSync(fd: Number)

external fun <TBuffer : dynamic> write(fd: Number, buffer: TBuffer, offset: Number?, length: Number?, position: Number?, callback: (err: NodeJS.ErrnoException?, written: Number, buffer: TBuffer) -> Unit)

external fun <TBuffer : dynamic> write(fd: Number, buffer: TBuffer, offset: Number?, length: Number?, callback: (err: NodeJS.ErrnoException?, written: Number, buffer: TBuffer) -> Unit)

external fun <TBuffer : dynamic> write(fd: Number, buffer: TBuffer, offset: Number?, callback: (err: NodeJS.ErrnoException?, written: Number, buffer: TBuffer) -> Unit)

external fun <TBuffer : dynamic> write(fd: Number, buffer: TBuffer, callback: (err: NodeJS.ErrnoException?, written: Number, buffer: TBuffer) -> Unit)

external fun write(fd: Number, string: Any, position: Number?, encoding: String?, callback: (err: NodeJS.ErrnoException?, written: Number, str: String) -> Unit)

external fun write(fd: Number, string: Any, position: Number?, callback: (err: NodeJS.ErrnoException?, written: Number, str: String) -> Unit)

external fun write(fd: Number, string: Any, callback: (err: NodeJS.ErrnoException?, written: Number, str: String) -> Unit)

external fun writeSync(fd: Number, buffer: Uint8Array, offset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */, position: Number? = definedExternally /* null */): Number

external fun writeSync(fd: Number, buffer: Uint8ClampedArray, offset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */, position: Number? = definedExternally /* null */): Number

external fun writeSync(fd: Number, buffer: Uint16Array, offset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */, position: Number? = definedExternally /* null */): Number

external fun writeSync(fd: Number, buffer: Uint32Array, offset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */, position: Number? = definedExternally /* null */): Number

external fun writeSync(fd: Number, buffer: Int8Array, offset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */, position: Number? = definedExternally /* null */): Number

external fun writeSync(fd: Number, buffer: Int16Array, offset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */, position: Number? = definedExternally /* null */): Number

external fun writeSync(fd: Number, buffer: Int32Array, offset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */, position: Number? = definedExternally /* null */): Number

external fun writeSync(fd: Number, buffer: Float32Array, offset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */, position: Number? = definedExternally /* null */): Number

external fun writeSync(fd: Number, buffer: Float64Array, offset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */, position: Number? = definedExternally /* null */): Number

external fun writeSync(fd: Number, buffer: DataView, offset: Number? = definedExternally /* null */, length: Number? = definedExternally /* null */, position: Number? = definedExternally /* null */): Number

external fun writeSync(fd: Number, string: Any, position: Number? = definedExternally /* null */, encoding: String? = definedExternally /* null */): Number

external fun <TBuffer : dynamic> read(fd: Number, buffer: TBuffer, offset: Number, length: Number, position: Number?, callback: (err: NodeJS.ErrnoException?, bytesRead: Number, buffer: TBuffer) -> Unit)

external fun readSync(fd: Number, buffer: Uint8Array, offset: Number, length: Number, position: Number?): Number

external fun readSync(fd: Number, buffer: Uint8ClampedArray, offset: Number, length: Number, position: Number?): Number

external fun readSync(fd: Number, buffer: Uint16Array, offset: Number, length: Number, position: Number?): Number

external fun readSync(fd: Number, buffer: Uint32Array, offset: Number, length: Number, position: Number?): Number

external fun readSync(fd: Number, buffer: Int8Array, offset: Number, length: Number, position: Number?): Number

external fun readSync(fd: Number, buffer: Int16Array, offset: Number, length: Number, position: Number?): Number

external fun readSync(fd: Number, buffer: Int32Array, offset: Number, length: Number, position: Number?): Number

external fun readSync(fd: Number, buffer: Float32Array, offset: Number, length: Number, position: Number?): Number

external fun readSync(fd: Number, buffer: Float64Array, offset: Number, length: Number, position: Number?): Number

external fun readSync(fd: Number, buffer: DataView, offset: Number, length: Number, position: Number?): Number

external interface `T$26` {
    var encoding: Nothing?
        get() = definedExternally
        set(value) = definedExternally
    var flag: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun readFile(path: String, options: `T$26`, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external fun readFile(path: String, options: Nothing?, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external fun readFile(path: Buffer, options: `T$26`, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external fun readFile(path: Buffer, options: Nothing?, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external fun readFile(path: URL, options: `T$26`, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external fun readFile(path: URL, options: Nothing?, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external fun readFile(path: Number, options: `T$26`, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external fun readFile(path: Number, options: Nothing?, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external interface `T$27` {
    var encoding: String
    var flag: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun readFile(path: String, options: `T$27`, callback: (err: NodeJS.ErrnoException?, data: String) -> Unit)

external fun readFile(path: String, options: String, callback: (err: NodeJS.ErrnoException?, data: String) -> Unit)

external fun readFile(path: Buffer, options: `T$27`, callback: (err: NodeJS.ErrnoException?, data: String) -> Unit)

external fun readFile(path: Buffer, options: String, callback: (err: NodeJS.ErrnoException?, data: String) -> Unit)

external fun readFile(path: URL, options: `T$27`, callback: (err: NodeJS.ErrnoException?, data: String) -> Unit)

external fun readFile(path: URL, options: String, callback: (err: NodeJS.ErrnoException?, data: String) -> Unit)

external fun readFile(path: Number, options: `T$27`, callback: (err: NodeJS.ErrnoException?, data: String) -> Unit)

external fun readFile(path: Number, options: String, callback: (err: NodeJS.ErrnoException?, data: String) -> Unit)

external interface `T$28` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var flag: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun readFile(path: String, options: dynamic /* `T$28` | String | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, data: dynamic /* String | Buffer */) -> Unit)

external fun readFile(path: Buffer, options: dynamic /* `T$28` | String | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, data: dynamic /* String | Buffer */) -> Unit)

external fun readFile(path: URL, options: dynamic /* `T$28` | String | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, data: dynamic /* String | Buffer */) -> Unit)

external fun readFile(path: Number, options: dynamic /* `T$28` | String | Nothing? | Nothing? */, callback: (err: NodeJS.ErrnoException?, data: dynamic /* String | Buffer */) -> Unit)

external fun readFile(path: String, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external fun readFile(path: Buffer, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external fun readFile(path: URL, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external fun readFile(path: Number, callback: (err: NodeJS.ErrnoException?, data: Buffer) -> Unit)

external fun readFileSync(path: String, options: `T$26`? = definedExternally /* null */): Buffer

external fun readFileSync(path: String, options: Nothing? = definedExternally /* null */): Buffer

external fun readFileSync(path: Buffer, options: `T$26`? = definedExternally /* null */): Buffer

external fun readFileSync(path: Buffer, options: Nothing? = definedExternally /* null */): Buffer

external fun readFileSync(path: URL, options: `T$26`? = definedExternally /* null */): Buffer

external fun readFileSync(path: URL, options: Nothing? = definedExternally /* null */): Buffer

external fun readFileSync(path: Number, options: `T$26`? = definedExternally /* null */): Buffer

external fun readFileSync(path: Number, options: Nothing? = definedExternally /* null */): Buffer

external fun readFileSync(path: String, options: `T$27`): String

external fun readFileSync(path: String, options: String): String

external fun readFileSync(path: Buffer, options: `T$27`): String

external fun readFileSync(path: Buffer, options: String): String

external fun readFileSync(path: URL, options: `T$27`): String

external fun readFileSync(path: URL, options: String): String

external fun readFileSync(path: Number, options: `T$27`): String

external fun readFileSync(path: Number, options: String): String

external fun readFileSync(path: String, options: `T$28`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readFileSync(path: String, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readFileSync(path: String, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readFileSync(path: Buffer, options: `T$28`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readFileSync(path: Buffer, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readFileSync(path: Buffer, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readFileSync(path: URL, options: `T$28`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readFileSync(path: URL, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readFileSync(path: URL, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readFileSync(path: Number, options: `T$28`? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readFileSync(path: Number, options: String? = definedExternally /* null */): dynamic /* String | Buffer */

external fun readFileSync(path: Number, options: Nothing? = definedExternally /* null */): dynamic /* String | Buffer */

external interface `T$54` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var mode: dynamic /* Number | String */
        get() = definedExternally
        set(value) = definedExternally
    var flag: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun writeFile(path: String, data: Any, options: `T$54`, callback: NoParamCallback)

external fun writeFile(path: String, data: Any, options: String, callback: NoParamCallback)

external fun writeFile(path: String, data: Any, options: Nothing?, callback: NoParamCallback)

external fun writeFile(path: Buffer, data: Any, options: `T$54`, callback: NoParamCallback)

external fun writeFile(path: Buffer, data: Any, options: String, callback: NoParamCallback)

external fun writeFile(path: Buffer, data: Any, options: Nothing?, callback: NoParamCallback)

external fun writeFile(path: URL, data: Any, options: `T$54`, callback: NoParamCallback)

external fun writeFile(path: URL, data: Any, options: String, callback: NoParamCallback)

external fun writeFile(path: URL, data: Any, options: Nothing?, callback: NoParamCallback)

external fun writeFile(path: Number, data: Any, options: `T$54`, callback: NoParamCallback)

external fun writeFile(path: Number, data: Any, options: String, callback: NoParamCallback)

external fun writeFile(path: Number, data: Any, options: Nothing?, callback: NoParamCallback)

external fun writeFile(path: String, data: Any, callback: NoParamCallback)

external fun writeFile(path: Buffer, data: Any, callback: NoParamCallback)

external fun writeFile(path: URL, data: Any, callback: NoParamCallback)

external fun writeFile(path: Number, data: Any, callback: NoParamCallback)

external fun writeFileSync(path: String, data: Any, options: `T$54` = definedExternally /* null */)

external fun writeFileSync(path: String, data: Any, options: String = definedExternally /* null */)

external fun writeFileSync(path: String, data: Any, options: Nothing? = definedExternally /* null */)

external fun writeFileSync(path: Buffer, data: Any, options: `T$54` = definedExternally /* null */)

external fun writeFileSync(path: Buffer, data: Any, options: String = definedExternally /* null */)

external fun writeFileSync(path: Buffer, data: Any, options: Nothing? = definedExternally /* null */)

external fun writeFileSync(path: URL, data: Any, options: `T$54` = definedExternally /* null */)

external fun writeFileSync(path: URL, data: Any, options: String = definedExternally /* null */)

external fun writeFileSync(path: URL, data: Any, options: Nothing? = definedExternally /* null */)

external fun writeFileSync(path: Number, data: Any, options: `T$54` = definedExternally /* null */)

external fun writeFileSync(path: Number, data: Any, options: String = definedExternally /* null */)

external fun writeFileSync(path: Number, data: Any, options: Nothing? = definedExternally /* null */)

external fun appendFile(file: String, data: Any, options: `T$54`, callback: NoParamCallback)

external fun appendFile(file: String, data: Any, options: String, callback: NoParamCallback)

external fun appendFile(file: String, data: Any, options: Nothing?, callback: NoParamCallback)

external fun appendFile(file: Buffer, data: Any, options: `T$54`, callback: NoParamCallback)

external fun appendFile(file: Buffer, data: Any, options: String, callback: NoParamCallback)

external fun appendFile(file: Buffer, data: Any, options: Nothing?, callback: NoParamCallback)

external fun appendFile(file: URL, data: Any, options: `T$54`, callback: NoParamCallback)

external fun appendFile(file: URL, data: Any, options: String, callback: NoParamCallback)

external fun appendFile(file: URL, data: Any, options: Nothing?, callback: NoParamCallback)

external fun appendFile(file: Number, data: Any, options: `T$54`, callback: NoParamCallback)

external fun appendFile(file: Number, data: Any, options: String, callback: NoParamCallback)

external fun appendFile(file: Number, data: Any, options: Nothing?, callback: NoParamCallback)

external fun appendFile(file: String, data: Any, callback: NoParamCallback)

external fun appendFile(file: Buffer, data: Any, callback: NoParamCallback)

external fun appendFile(file: URL, data: Any, callback: NoParamCallback)

external fun appendFile(file: Number, data: Any, callback: NoParamCallback)

external fun appendFileSync(file: String, data: Any, options: `T$54` = definedExternally /* null */)

external fun appendFileSync(file: String, data: Any, options: String = definedExternally /* null */)

external fun appendFileSync(file: String, data: Any, options: Nothing? = definedExternally /* null */)

external fun appendFileSync(file: Buffer, data: Any, options: `T$54` = definedExternally /* null */)

external fun appendFileSync(file: Buffer, data: Any, options: String = definedExternally /* null */)

external fun appendFileSync(file: Buffer, data: Any, options: Nothing? = definedExternally /* null */)

external fun appendFileSync(file: URL, data: Any, options: `T$54` = definedExternally /* null */)

external fun appendFileSync(file: URL, data: Any, options: String = definedExternally /* null */)

external fun appendFileSync(file: URL, data: Any, options: Nothing? = definedExternally /* null */)

external fun appendFileSync(file: Number, data: Any, options: `T$54` = definedExternally /* null */)

external fun appendFileSync(file: Number, data: Any, options: String = definedExternally /* null */)

external fun appendFileSync(file: Number, data: Any, options: Nothing? = definedExternally /* null */)

external interface `T$32` {
    var persistent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var interval: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external fun watchFile(filename: String, options: `T$32`, listener: (curr: Stats, prev: Stats) -> Unit)

external fun watchFile(filename: String, options: Nothing?, listener: (curr: Stats, prev: Stats) -> Unit)

external fun watchFile(filename: Buffer, options: `T$32`, listener: (curr: Stats, prev: Stats) -> Unit)

external fun watchFile(filename: Buffer, options: Nothing?, listener: (curr: Stats, prev: Stats) -> Unit)

external fun watchFile(filename: URL, options: `T$32`, listener: (curr: Stats, prev: Stats) -> Unit)

external fun watchFile(filename: URL, options: Nothing?, listener: (curr: Stats, prev: Stats) -> Unit)

external fun watchFile(filename: String, listener: (curr: Stats, prev: Stats) -> Unit)

external fun watchFile(filename: Buffer, listener: (curr: Stats, prev: Stats) -> Unit)

external fun watchFile(filename: URL, listener: (curr: Stats, prev: Stats) -> Unit)

external fun unwatchFile(filename: String, listener: ((curr: Stats, prev: Stats) -> Unit)? = definedExternally /* null */)

external fun unwatchFile(filename: Buffer, listener: ((curr: Stats, prev: Stats) -> Unit)? = definedExternally /* null */)

external fun unwatchFile(filename: URL, listener: ((curr: Stats, prev: Stats) -> Unit)? = definedExternally /* null */)

external interface `T$33` {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
        get() = definedExternally
        set(value) = definedExternally
    var persistent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var recursive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external fun watch(filename: String, options: dynamic /* `T$33` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, listener: ((event: String, filename: String) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: Buffer, options: dynamic /* `T$33` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, listener: ((event: String, filename: String) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: URL, options: dynamic /* `T$33` | "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | Nothing? | Nothing? */, listener: ((event: String, filename: String) -> Unit)? = definedExternally /* null */): FSWatcher

external interface `T$34` {
    var encoding: String /* "buffer" */
    var persistent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var recursive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external fun watch(filename: String, options: `T$34`, listener: ((event: String, filename: Buffer) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: String, options: String /* "buffer" */, listener: ((event: String, filename: Buffer) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: Buffer, options: `T$34`, listener: ((event: String, filename: Buffer) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: Buffer, options: String /* "buffer" */, listener: ((event: String, filename: Buffer) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: URL, options: `T$34`, listener: ((event: String, filename: Buffer) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: URL, options: String /* "buffer" */, listener: ((event: String, filename: Buffer) -> Unit)? = definedExternally /* null */): FSWatcher

external interface `T$35` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var persistent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var recursive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external fun watch(filename: String, options: `T$35`, listener: ((event: String, filename: dynamic /* String | Buffer */) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: String, options: String, listener: ((event: String, filename: dynamic /* String | Buffer */) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: String, options: Nothing?, listener: ((event: String, filename: dynamic /* String | Buffer */) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: Buffer, options: `T$35`, listener: ((event: String, filename: dynamic /* String | Buffer */) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: Buffer, options: String, listener: ((event: String, filename: dynamic /* String | Buffer */) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: Buffer, options: Nothing?, listener: ((event: String, filename: dynamic /* String | Buffer */) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: URL, options: `T$35`, listener: ((event: String, filename: dynamic /* String | Buffer */) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: URL, options: String, listener: ((event: String, filename: dynamic /* String | Buffer */) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: URL, options: Nothing?, listener: ((event: String, filename: dynamic /* String | Buffer */) -> Unit)? = definedExternally /* null */): FSWatcher

external fun watch(filename: String, listener: ((event: String, filename: String) -> Any)? = definedExternally /* null */): FSWatcher

external fun watch(filename: Buffer, listener: ((event: String, filename: String) -> Any)? = definedExternally /* null */): FSWatcher

external fun watch(filename: URL, listener: ((event: String, filename: String) -> Any)? = definedExternally /* null */): FSWatcher

external fun exists(path: String, callback: (exists: Boolean) -> Unit)

external fun exists(path: Buffer, callback: (exists: Boolean) -> Unit)

external fun exists(path: URL, callback: (exists: Boolean) -> Unit)

external fun existsSync(path: String): Boolean

external fun existsSync(path: Buffer): Boolean

external fun existsSync(path: URL): Boolean

external fun access(path: String, mode: Number?, callback: NoParamCallback)

external fun access(path: Buffer, mode: Number?, callback: NoParamCallback)

external fun access(path: URL, mode: Number?, callback: NoParamCallback)

external fun access(path: String, callback: NoParamCallback)

external fun access(path: Buffer, callback: NoParamCallback)

external fun access(path: URL, callback: NoParamCallback)

external fun accessSync(path: String, mode: Number? = definedExternally /* null */)

external fun accessSync(path: Buffer, mode: Number? = definedExternally /* null */)

external fun accessSync(path: URL, mode: Number? = definedExternally /* null */)

external interface `T$36` {
    var flags: String?
        get() = definedExternally
        set(value) = definedExternally
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var fd: Number?
        get() = definedExternally
        set(value) = definedExternally
    var mode: Number?
        get() = definedExternally
        set(value) = definedExternally
    var autoClose: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var emitClose: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var start: Number?
        get() = definedExternally
        set(value) = definedExternally
    var end: Number?
        get() = definedExternally
        set(value) = definedExternally
    var highWaterMark: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external fun createReadStream(path: String, options: String? = definedExternally /* null */): ReadStream

external fun createReadStream(path: String, options: `T$36`? = definedExternally /* null */): ReadStream

external fun createReadStream(path: Buffer, options: String? = definedExternally /* null */): ReadStream

external fun createReadStream(path: Buffer, options: `T$36`? = definedExternally /* null */): ReadStream

external fun createReadStream(path: URL, options: String? = definedExternally /* null */): ReadStream

external fun createReadStream(path: URL, options: `T$36`? = definedExternally /* null */): ReadStream

external interface `T$37` {
    var flags: String?
        get() = definedExternally
        set(value) = definedExternally
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var fd: Number?
        get() = definedExternally
        set(value) = definedExternally
    var mode: Number?
        get() = definedExternally
        set(value) = definedExternally
    var autoClose: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var start: Number?
        get() = definedExternally
        set(value) = definedExternally
    var highWaterMark: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external fun createWriteStream(path: String, options: String? = definedExternally /* null */): WriteStream

external fun createWriteStream(path: String, options: `T$37`? = definedExternally /* null */): WriteStream

external fun createWriteStream(path: Buffer, options: String? = definedExternally /* null */): WriteStream

external fun createWriteStream(path: Buffer, options: `T$37`? = definedExternally /* null */): WriteStream

external fun createWriteStream(path: URL, options: String? = definedExternally /* null */): WriteStream

external fun createWriteStream(path: URL, options: `T$37`? = definedExternally /* null */): WriteStream

external fun fdatasync(fd: Number, callback: NoParamCallback)

external fun fdatasyncSync(fd: Number)

external fun copyFile(src: String, dest: String, callback: NoParamCallback)

external fun copyFile(src: String, dest: Buffer, callback: NoParamCallback)

external fun copyFile(src: String, dest: URL, callback: NoParamCallback)

external fun copyFile(src: Buffer, dest: String, callback: NoParamCallback)

external fun copyFile(src: Buffer, dest: Buffer, callback: NoParamCallback)

external fun copyFile(src: Buffer, dest: URL, callback: NoParamCallback)

external fun copyFile(src: URL, dest: String, callback: NoParamCallback)

external fun copyFile(src: URL, dest: Buffer, callback: NoParamCallback)

external fun copyFile(src: URL, dest: URL, callback: NoParamCallback)

external fun copyFile(src: String, dest: String, flags: Number, callback: NoParamCallback)

external fun copyFile(src: String, dest: Buffer, flags: Number, callback: NoParamCallback)

external fun copyFile(src: String, dest: URL, flags: Number, callback: NoParamCallback)

external fun copyFile(src: Buffer, dest: String, flags: Number, callback: NoParamCallback)

external fun copyFile(src: Buffer, dest: Buffer, flags: Number, callback: NoParamCallback)

external fun copyFile(src: Buffer, dest: URL, flags: Number, callback: NoParamCallback)

external fun copyFile(src: URL, dest: String, flags: Number, callback: NoParamCallback)

external fun copyFile(src: URL, dest: Buffer, flags: Number, callback: NoParamCallback)

external fun copyFile(src: URL, dest: URL, flags: Number, callback: NoParamCallback)

external fun copyFileSync(src: String, dest: String, flags: Number? = definedExternally /* null */)

external fun copyFileSync(src: String, dest: Buffer, flags: Number? = definedExternally /* null */)

external fun copyFileSync(src: String, dest: URL, flags: Number? = definedExternally /* null */)

external fun copyFileSync(src: Buffer, dest: String, flags: Number? = definedExternally /* null */)

external fun copyFileSync(src: Buffer, dest: Buffer, flags: Number? = definedExternally /* null */)

external fun copyFileSync(src: Buffer, dest: URL, flags: Number? = definedExternally /* null */)

external fun copyFileSync(src: URL, dest: String, flags: Number? = definedExternally /* null */)

external fun copyFileSync(src: URL, dest: Buffer, flags: Number? = definedExternally /* null */)

external fun copyFileSync(src: URL, dest: URL, flags: Number? = definedExternally /* null */)

external fun writev(fd: Number, buffers: Array<dynamic /* Uint8Array | Uint8ClampedArray | Uint16Array | Uint32Array | Int8Array | Int16Array | Int32Array | Float32Array | Float64Array | DataView */>, cb: (err: NodeJS.ErrnoException?, bytesWritten: Number, buffers: Array<dynamic /* Uint8Array | Uint8ClampedArray | Uint16Array | Uint32Array | Int8Array | Int16Array | Int32Array | Float32Array | Float64Array | DataView */>) -> Unit)

external fun writev(fd: Number, buffers: Array<dynamic /* Uint8Array | Uint8ClampedArray | Uint16Array | Uint32Array | Int8Array | Int16Array | Int32Array | Float32Array | Float64Array | DataView */>, position: Number, cb: (err: NodeJS.ErrnoException?, bytesWritten: Number, buffers: Array<dynamic /* Uint8Array | Uint8ClampedArray | Uint16Array | Uint32Array | Int8Array | Int16Array | Int32Array | Float32Array | Float64Array | DataView */>) -> Unit)

external interface WriteVResult {
    var bytesWritten: Number
    var buffers: Array<dynamic /* Uint8Array | Uint8ClampedArray | Uint16Array | Uint32Array | Int8Array | Int16Array | Int32Array | Float32Array | Float64Array | DataView */>
}

external fun writevSync(fd: Number, buffers: Array<dynamic /* Uint8Array | Uint8ClampedArray | Uint16Array | Uint32Array | Int8Array | Int16Array | Int32Array | Float32Array | Float64Array | DataView */>, position: Number? = definedExternally /* null */): Number

external fun readlinkSync(path: String): String

external fun readlinkSync(path: Buffer): String

external fun readlinkSync(path: URL): String

external fun realpathSync(path: String): String

external fun realpathSync(path: Buffer): String

external fun realpathSync(path: URL): String

external fun mkdirSync(path: String)

external fun mkdirSync(path: Buffer)

external fun mkdirSync(path: URL)

external fun mkdtempSync(prefix: String): String

external fun readdirSync(path: String): Array<String>

external fun readdirSync(path: Buffer): Array<String>

external fun readdirSync(path: URL): Array<String>

external fun readFileSync(path: String): Buffer

external fun readFileSync(path: Buffer): Buffer

external fun readFileSync(path: URL): Buffer

external fun readFileSync(path: Number): Buffer

external fun writeFileSync(path: String, data: Any)

external fun writeFileSync(path: Buffer, data: Any)

external fun writeFileSync(path: URL, data: Any)

external fun writeFileSync(path: Number, data: Any)

external fun appendFileSync(file: String, data: Any)

external fun appendFileSync(file: Buffer, data: Any)

external fun appendFileSync(file: URL, data: Any)

external fun appendFileSync(file: Number, data: Any)

external fun createReadStream(path: String): ReadStream

external fun createReadStream(path: Buffer): ReadStream

external fun createReadStream(path: URL): ReadStream

external fun createWriteStream(path: String): WriteStream

external fun createWriteStream(path: Buffer): WriteStream

external fun createWriteStream(path: URL): WriteStream