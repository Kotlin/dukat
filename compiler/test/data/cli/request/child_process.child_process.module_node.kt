@file:JsModule("child_process")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package child_process

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

external interface ChildProcess : events.EventEmitter {
    var stdin: Writable?
    var stdout: Readable?
    var stderr: Readable?
    var channel: Pipe?
        get() = definedExternally
        set(value) = definedExternally
    var stdio: dynamic /* JsTuple<Writable?, Readable?, Readable?, dynamic, dynamic> */
    var killed: Boolean
    var pid: Number
    var connected: Boolean
    fun kill(signal: String? = definedExternally /* null */)
    fun send(message: Any, callback: ((error: Error?) -> Unit)? = definedExternally /* null */): Boolean
    fun send(message: Any, sendHandle: net.Socket? = definedExternally /* null */, callback: ((error: Error?) -> Unit)? = definedExternally /* null */): Boolean
    fun send(message: Any, sendHandle: net.Server? = definedExternally /* null */, callback: ((error: Error?) -> Unit)? = definedExternally /* null */): Boolean
    fun send(message: Any, sendHandle: net.Socket? = definedExternally /* null */, options: MessageOptions? = definedExternally /* null */, callback: ((error: Error?) -> Unit)? = definedExternally /* null */): Boolean
    fun send(message: Any, sendHandle: net.Server? = definedExternally /* null */, options: MessageOptions? = definedExternally /* null */, callback: ((error: Error?) -> Unit)? = definedExternally /* null */): Boolean
    fun disconnect()
    fun unref()
    fun ref()
    fun addListener(event: String, listener: (args: Array<Any>) -> Unit): ChildProcess /* this */
    fun addListener(event: String /* "close" */, listener: (code: Number, signal: String) -> Unit): ChildProcess /* this */
    fun addListener(event: String /* "disconnect" */, listener: () -> Unit): ChildProcess /* this */
    fun addListener(event: String /* "error" */, listener: (err: Error) -> Unit): ChildProcess /* this */
    fun addListener(event: String /* "exit" */, listener: (code: Number?, signal: String?) -> Unit): ChildProcess /* this */
    fun addListener(event: String /* "message" */, listener: (message: Any, sendHandle: dynamic /* net.Socket | net.Server */) -> Unit): ChildProcess /* this */
    fun emit(event: String, vararg args: Any): Boolean
    fun emit(event: Any, vararg args: Any): Boolean
    fun emit(event: String /* "close" */, code: Number, signal: String): Boolean
    fun emit(event: String /* "disconnect" */): Boolean
    fun emit(event: String /* "error" */, err: Error): Boolean
    fun emit(event: String /* "exit" */, code: Number?, signal: String?): Boolean
    fun emit(event: String /* "message" */, message: Any, sendHandle: net.Socket): Boolean
    fun emit(event: String /* "message" */, message: Any, sendHandle: net.Server): Boolean
    fun on(event: String, listener: (args: Array<Any>) -> Unit): ChildProcess /* this */
    fun on(event: String /* "close" */, listener: (code: Number, signal: String) -> Unit): ChildProcess /* this */
    fun on(event: String /* "disconnect" */, listener: () -> Unit): ChildProcess /* this */
    fun on(event: String /* "error" */, listener: (err: Error) -> Unit): ChildProcess /* this */
    fun on(event: String /* "exit" */, listener: (code: Number?, signal: String?) -> Unit): ChildProcess /* this */
    fun on(event: String /* "message" */, listener: (message: Any, sendHandle: dynamic /* net.Socket | net.Server */) -> Unit): ChildProcess /* this */
    fun once(event: String, listener: (args: Array<Any>) -> Unit): ChildProcess /* this */
    fun once(event: String /* "close" */, listener: (code: Number, signal: String) -> Unit): ChildProcess /* this */
    fun once(event: String /* "disconnect" */, listener: () -> Unit): ChildProcess /* this */
    fun once(event: String /* "error" */, listener: (err: Error) -> Unit): ChildProcess /* this */
    fun once(event: String /* "exit" */, listener: (code: Number?, signal: String?) -> Unit): ChildProcess /* this */
    fun once(event: String /* "message" */, listener: (message: Any, sendHandle: dynamic /* net.Socket | net.Server */) -> Unit): ChildProcess /* this */
    fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): ChildProcess /* this */
    fun prependListener(event: String /* "close" */, listener: (code: Number, signal: String) -> Unit): ChildProcess /* this */
    fun prependListener(event: String /* "disconnect" */, listener: () -> Unit): ChildProcess /* this */
    fun prependListener(event: String /* "error" */, listener: (err: Error) -> Unit): ChildProcess /* this */
    fun prependListener(event: String /* "exit" */, listener: (code: Number?, signal: String?) -> Unit): ChildProcess /* this */
    fun prependListener(event: String /* "message" */, listener: (message: Any, sendHandle: dynamic /* net.Socket | net.Server */) -> Unit): ChildProcess /* this */
    fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): ChildProcess /* this */
    fun prependOnceListener(event: String /* "close" */, listener: (code: Number, signal: String) -> Unit): ChildProcess /* this */
    fun prependOnceListener(event: String /* "disconnect" */, listener: () -> Unit): ChildProcess /* this */
    fun prependOnceListener(event: String /* "error" */, listener: (err: Error) -> Unit): ChildProcess /* this */
    fun prependOnceListener(event: String /* "exit" */, listener: (code: Number?, signal: String?) -> Unit): ChildProcess /* this */
    fun prependOnceListener(event: String /* "message" */, listener: (message: Any, sendHandle: dynamic /* net.Socket | net.Server */) -> Unit): ChildProcess /* this */
}

external interface ChildProcessWithoutNullStreams : ChildProcess {
    var stdin: Writable
    var stdout: Readable
    var stderr: Readable
    override var stdio: dynamic /* JsTuple<Writable, Readable, Readable, dynamic, dynamic> */
}

external interface ChildProcessByStdio<I : Writable?, O : Readable?, E : Readable?> : ChildProcess {
    var stdin: I
    var stdout: O
    var stderr: E
    override var stdio: dynamic /* JsTuple<I, O, E, dynamic, dynamic> */
}

external interface MessageOptions {
    var keepOpen: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ProcessEnvOptions {
    var uid: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gid: Number?
        get() = definedExternally
        set(value) = definedExternally
    var cwd: String?
        get() = definedExternally
        set(value) = definedExternally
    var env: NodeJS.ProcessEnv?
        get() = definedExternally
        set(value) = definedExternally
}

external interface CommonOptions : ProcessEnvOptions {
    var windowsHide: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SpawnOptions : CommonOptions {
    var argv0: String?
        get() = definedExternally
        set(value) = definedExternally
    var stdio: dynamic /* "pipe" | "ignore" | "inherit" | Array<dynamic /* "pipe" | "ipc" | "ignore" | "inherit" | Stream | Number | Nothing? | Nothing? */> */
        get() = definedExternally
        set(value) = definedExternally
    var detached: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var shell: dynamic /* Boolean | String */
        get() = definedExternally
        set(value) = definedExternally
    var windowsVerbatimArguments: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SpawnOptionsWithoutStdio : SpawnOptions {
    override var stdio: dynamic /* 'pipe' | Array<String?> */
        get() = definedExternally
        set(value) = definedExternally
}

external interface SpawnOptionsWithStdioTuple<Stdin : dynamic, Stdout : dynamic, Stderr : dynamic> : SpawnOptions {
    override var stdio: dynamic /* JsTuple<Stdin, Stdout, Stderr> */
}

external fun spawn(command: String, options: SpawnOptionsWithoutStdio? = definedExternally /* null */): ChildProcessWithoutNullStreams

external fun spawn(command: String, options: SpawnOptionsWithStdioTuple<String?, String?, String?>): ChildProcessByStdio<Writable, Readable, Readable>

external fun spawn(command: String, options: SpawnOptionsWithStdioTuple<String?, String?, dynamic /* 'inherit' | 'ignore' | Stream */>): ChildProcessByStdio<Writable, Readable, Nothing?>

external fun spawn(command: String, options: SpawnOptionsWithStdioTuple<String?, dynamic /* 'inherit' | 'ignore' | Stream */, String?>): ChildProcessByStdio<Writable, Nothing?, Readable>

external fun spawn(command: String, options: SpawnOptionsWithStdioTuple<dynamic /* 'inherit' | 'ignore' | Stream */, String?, String?>): ChildProcessByStdio<Nothing?, Readable, Readable>

external fun spawn(command: String, options: SpawnOptionsWithStdioTuple<String?, dynamic /* 'inherit' | 'ignore' | Stream */, dynamic /* 'inherit' | 'ignore' | Stream */>): ChildProcessByStdio<Writable, Nothing?, Nothing?>

external fun spawn(command: String, options: SpawnOptionsWithStdioTuple<dynamic /* 'inherit' | 'ignore' | Stream */, String?, dynamic /* 'inherit' | 'ignore' | Stream */>): ChildProcessByStdio<Nothing?, Readable, Nothing?>

external fun spawn(command: String, options: SpawnOptionsWithStdioTuple<dynamic /* 'inherit' | 'ignore' | Stream */, dynamic /* 'inherit' | 'ignore' | Stream */, String?>): ChildProcessByStdio<Nothing?, Nothing?, Readable>

external fun spawn(command: String, options: SpawnOptionsWithStdioTuple<dynamic /* 'inherit' | 'ignore' | Stream */, dynamic /* 'inherit' | 'ignore' | Stream */, dynamic /* 'inherit' | 'ignore' | Stream */>): ChildProcessByStdio<Nothing?, Nothing?, Nothing?>

external fun spawn(command: String, options: SpawnOptions): ChildProcess

external fun spawn(command: String, args: ReadonlyArray<String>? = definedExternally /* null */, options: SpawnOptionsWithoutStdio? = definedExternally /* null */): ChildProcessWithoutNullStreams

external fun spawn(command: String, args: ReadonlyArray<String>, options: SpawnOptionsWithStdioTuple<String?, String?, String?>): ChildProcessByStdio<Writable, Readable, Readable>

external fun spawn(command: String, args: ReadonlyArray<String>, options: SpawnOptionsWithStdioTuple<String?, String?, dynamic /* 'inherit' | 'ignore' | Stream */>): ChildProcessByStdio<Writable, Readable, Nothing?>

external fun spawn(command: String, args: ReadonlyArray<String>, options: SpawnOptionsWithStdioTuple<String?, dynamic /* 'inherit' | 'ignore' | Stream */, String?>): ChildProcessByStdio<Writable, Nothing?, Readable>

external fun spawn(command: String, args: ReadonlyArray<String>, options: SpawnOptionsWithStdioTuple<dynamic /* 'inherit' | 'ignore' | Stream */, String?, String?>): ChildProcessByStdio<Nothing?, Readable, Readable>

external fun spawn(command: String, args: ReadonlyArray<String>, options: SpawnOptionsWithStdioTuple<String?, dynamic /* 'inherit' | 'ignore' | Stream */, dynamic /* 'inherit' | 'ignore' | Stream */>): ChildProcessByStdio<Writable, Nothing?, Nothing?>

external fun spawn(command: String, args: ReadonlyArray<String>, options: SpawnOptionsWithStdioTuple<dynamic /* 'inherit' | 'ignore' | Stream */, String?, dynamic /* 'inherit' | 'ignore' | Stream */>): ChildProcessByStdio<Nothing?, Readable, Nothing?>

external fun spawn(command: String, args: ReadonlyArray<String>, options: SpawnOptionsWithStdioTuple<dynamic /* 'inherit' | 'ignore' | Stream */, dynamic /* 'inherit' | 'ignore' | Stream */, String?>): ChildProcessByStdio<Nothing?, Nothing?, Readable>

external fun spawn(command: String, args: ReadonlyArray<String>, options: SpawnOptionsWithStdioTuple<dynamic /* 'inherit' | 'ignore' | Stream */, dynamic /* 'inherit' | 'ignore' | Stream */, dynamic /* 'inherit' | 'ignore' | Stream */>): ChildProcessByStdio<Nothing?, Nothing?, Nothing?>

external fun spawn(command: String, args: ReadonlyArray<String>, options: SpawnOptions): ChildProcess

external interface ExecOptions : CommonOptions {
    var shell: String?
        get() = definedExternally
        set(value) = definedExternally
    var maxBuffer: Number?
        get() = definedExternally
        set(value) = definedExternally
    var killSignal: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ExecOptionsWithStringEncoding : ExecOptions {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
}

external interface ExecOptionsWithBufferEncoding : ExecOptions {
    var encoding: String?
}

external interface ExecException : Error {
    var cmd: String?
        get() = definedExternally
        set(value) = definedExternally
    var killed: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var code: Number?
        get() = definedExternally
        set(value) = definedExternally
    var signal: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun exec(command: String, callback: ((error: ExecException?, stdout: String, stderr: String) -> Unit)? = definedExternally /* null */): ChildProcess

external interface `T$0` {
    var encoding: String?
}

external fun exec(command: String, options: `T$0` /* `T$0` & ExecOptions */, callback: ((error: ExecException?, stdout: Buffer, stderr: Buffer) -> Unit)? = definedExternally /* null */): ChildProcess

external interface `T$1` {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
}

external fun exec(command: String, options: `T$1` /* `T$1` & ExecOptions */, callback: ((error: ExecException?, stdout: String, stderr: String) -> Unit)? = definedExternally /* null */): ChildProcess

external interface `T$2` {
    var encoding: String
}

external fun exec(command: String, options: `T$2` /* `T$2` & ExecOptions */, callback: ((error: ExecException?, stdout: dynamic /* String | Buffer */, stderr: dynamic /* String | Buffer */) -> Unit)? = definedExternally /* null */): ChildProcess

external fun exec(command: String, options: ExecOptions, callback: ((error: ExecException?, stdout: String, stderr: String) -> Unit)? = definedExternally /* null */): ChildProcess

external interface `T$3` {
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

external fun exec(command: String, options: `T$3` /* `T$3` & ExecOptions */, callback: ((error: ExecException?, stdout: dynamic /* String | Buffer */, stderr: dynamic /* String | Buffer */) -> Unit)? = definedExternally /* null */): ChildProcess

external interface PromiseWithChild<T> : Promise<T> {
    var child: ChildProcess
}

external interface ExecFileOptions : CommonOptions {
    var maxBuffer: Number?
        get() = definedExternally
        set(value) = definedExternally
    var killSignal: String?
        get() = definedExternally
        set(value) = definedExternally
    var windowsVerbatimArguments: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var shell: dynamic /* Boolean | String */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ExecFileOptionsWithStringEncoding : ExecFileOptions {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
}

external interface ExecFileOptionsWithBufferEncoding : ExecFileOptions {
    var encoding: String?
}

external interface ExecFileOptionsWithOtherEncoding : ExecFileOptions {
    var encoding: String
}

external fun execFile(file: String): ChildProcess

external fun execFile(file: String, options: `T$3` /* `T$3` & ExecFileOptions */): ChildProcess

external fun execFile(file: String, args: ReadonlyArray<String>? = definedExternally /* null */): ChildProcess

external fun execFile(file: String, args: ReadonlyArray<String>?, options: `T$3` /* `T$3` & ExecFileOptions */): ChildProcess

external fun execFile(file: String, callback: (error: Error?, stdout: String, stderr: String) -> Unit): ChildProcess

external fun execFile(file: String, args: ReadonlyArray<String>?, callback: (error: Error?, stdout: String, stderr: String) -> Unit): ChildProcess

external fun execFile(file: String, options: ExecFileOptionsWithBufferEncoding, callback: (error: Error?, stdout: Buffer, stderr: Buffer) -> Unit): ChildProcess

external fun execFile(file: String, args: ReadonlyArray<String>?, options: ExecFileOptionsWithBufferEncoding, callback: (error: Error?, stdout: Buffer, stderr: Buffer) -> Unit): ChildProcess

external fun execFile(file: String, options: ExecFileOptionsWithStringEncoding, callback: (error: Error?, stdout: String, stderr: String) -> Unit): ChildProcess

external fun execFile(file: String, args: ReadonlyArray<String>?, options: ExecFileOptionsWithStringEncoding, callback: (error: Error?, stdout: String, stderr: String) -> Unit): ChildProcess

external fun execFile(file: String, options: ExecFileOptionsWithOtherEncoding, callback: (error: Error?, stdout: dynamic /* String | Buffer */, stderr: dynamic /* String | Buffer */) -> Unit): ChildProcess

external fun execFile(file: String, args: ReadonlyArray<String>?, options: ExecFileOptionsWithOtherEncoding, callback: (error: Error?, stdout: dynamic /* String | Buffer */, stderr: dynamic /* String | Buffer */) -> Unit): ChildProcess

external fun execFile(file: String, options: ExecFileOptions, callback: (error: Error?, stdout: String, stderr: String) -> Unit): ChildProcess

external fun execFile(file: String, args: ReadonlyArray<String>?, options: ExecFileOptions, callback: (error: Error?, stdout: String, stderr: String) -> Unit): ChildProcess

external fun execFile(file: String, options: `T$3` /* `T$3` & ExecFileOptions */, callback: ((error: Error?, stdout: dynamic /* String | Buffer */, stderr: dynamic /* String | Buffer */) -> Unit)?): ChildProcess

external fun execFile(file: String, args: ReadonlyArray<String>?, options: `T$3` /* `T$3` & ExecFileOptions */, callback: ((error: Error?, stdout: dynamic /* String | Buffer */, stderr: dynamic /* String | Buffer */) -> Unit)?): ChildProcess

external interface ForkOptions : ProcessEnvOptions {
    var execPath: String?
        get() = definedExternally
        set(value) = definedExternally
    var execArgv: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var silent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stdio: dynamic /* "pipe" | "ignore" | "inherit" | Array<dynamic /* "pipe" | "ipc" | "ignore" | "inherit" | Stream | Number | Nothing? | Nothing? */> */
        get() = definedExternally
        set(value) = definedExternally
    var detached: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var windowsVerbatimArguments: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external fun fork(modulePath: String, args: ReadonlyArray<String>? = definedExternally /* null */, options: ForkOptions? = definedExternally /* null */): ChildProcess

external interface SpawnSyncOptions : CommonOptions {
    var argv0: String?
        get() = definedExternally
        set(value) = definedExternally
    var input: dynamic /* String | Uint8Array | Uint8ClampedArray | Uint16Array | Uint32Array | Int8Array | Int16Array | Int32Array | Float32Array | Float64Array | DataView */
        get() = definedExternally
        set(value) = definedExternally
    var stdio: dynamic /* "pipe" | "ignore" | "inherit" | Array<dynamic /* "pipe" | "ipc" | "ignore" | "inherit" | Stream | Number | Nothing? | Nothing? */> */
        get() = definedExternally
        set(value) = definedExternally
    var killSignal: dynamic /* String | Number */
        get() = definedExternally
        set(value) = definedExternally
    var maxBuffer: Number?
        get() = definedExternally
        set(value) = definedExternally
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var shell: dynamic /* Boolean | String */
        get() = definedExternally
        set(value) = definedExternally
    var windowsVerbatimArguments: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SpawnSyncOptionsWithStringEncoding : SpawnSyncOptions {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
}

external interface SpawnSyncOptionsWithBufferEncoding : SpawnSyncOptions {
    var encoding: String
}

external interface SpawnSyncReturns<T> {
    var pid: Number
    var output: Array<String>
    var stdout: T
    var stderr: T
    var status: Number?
    var signal: String?
    var error: Error?
        get() = definedExternally
        set(value) = definedExternally
}

external fun spawnSync(command: String): SpawnSyncReturns<Buffer>

external fun spawnSync(command: String, options: SpawnSyncOptionsWithStringEncoding? = definedExternally /* null */): SpawnSyncReturns<String>

external fun spawnSync(command: String, options: SpawnSyncOptionsWithBufferEncoding? = definedExternally /* null */): SpawnSyncReturns<Buffer>

external fun spawnSync(command: String, options: SpawnSyncOptions? = definedExternally /* null */): SpawnSyncReturns<Buffer>

external fun spawnSync(command: String, args: ReadonlyArray<String>? = definedExternally /* null */, options: SpawnSyncOptionsWithStringEncoding? = definedExternally /* null */): SpawnSyncReturns<String>

external fun spawnSync(command: String, args: ReadonlyArray<String>? = definedExternally /* null */, options: SpawnSyncOptionsWithBufferEncoding? = definedExternally /* null */): SpawnSyncReturns<Buffer>

external fun spawnSync(command: String, args: ReadonlyArray<String>? = definedExternally /* null */, options: SpawnSyncOptions? = definedExternally /* null */): SpawnSyncReturns<Buffer>

external interface ExecSyncOptions : CommonOptions {
    var input: dynamic /* String | Uint8Array */
        get() = definedExternally
        set(value) = definedExternally
    var stdio: dynamic /* "pipe" | "ignore" | "inherit" | Array<dynamic /* "pipe" | "ipc" | "ignore" | "inherit" | Stream | Number | Nothing? | Nothing? */> */
        get() = definedExternally
        set(value) = definedExternally
    var shell: String?
        get() = definedExternally
        set(value) = definedExternally
    var killSignal: dynamic /* String | Number */
        get() = definedExternally
        set(value) = definedExternally
    var maxBuffer: Number?
        get() = definedExternally
        set(value) = definedExternally
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ExecSyncOptionsWithStringEncoding : ExecSyncOptions {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
}

external interface ExecSyncOptionsWithBufferEncoding : ExecSyncOptions {
    var encoding: String
}

external fun execSync(command: String): Buffer

external fun execSync(command: String, options: ExecSyncOptionsWithStringEncoding? = definedExternally /* null */): String

external fun execSync(command: String, options: ExecSyncOptionsWithBufferEncoding? = definedExternally /* null */): Buffer

external fun execSync(command: String, options: ExecSyncOptions? = definedExternally /* null */): Buffer

external interface ExecFileSyncOptions : CommonOptions {
    var input: dynamic /* String | Uint8Array | Uint8ClampedArray | Uint16Array | Uint32Array | Int8Array | Int16Array | Int32Array | Float32Array | Float64Array | DataView */
        get() = definedExternally
        set(value) = definedExternally
    var stdio: dynamic /* "pipe" | "ignore" | "inherit" | Array<dynamic /* "pipe" | "ipc" | "ignore" | "inherit" | Stream | Number | Nothing? | Nothing? */> */
        get() = definedExternally
        set(value) = definedExternally
    var killSignal: dynamic /* String | Number */
        get() = definedExternally
        set(value) = definedExternally
    var maxBuffer: Number?
        get() = definedExternally
        set(value) = definedExternally
    var encoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var shell: dynamic /* Boolean | String */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ExecFileSyncOptionsWithStringEncoding : ExecFileSyncOptions {
    var encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */
}

external interface ExecFileSyncOptionsWithBufferEncoding : ExecFileSyncOptions {
    var encoding: String
}

external fun execFileSync(command: String): Buffer

external fun execFileSync(command: String, options: ExecFileSyncOptionsWithStringEncoding? = definedExternally /* null */): String

external fun execFileSync(command: String, options: ExecFileSyncOptionsWithBufferEncoding? = definedExternally /* null */): Buffer

external fun execFileSync(command: String, options: ExecFileSyncOptions? = definedExternally /* null */): Buffer

external fun execFileSync(command: String, args: ReadonlyArray<String>? = definedExternally /* null */, options: ExecFileSyncOptionsWithStringEncoding? = definedExternally /* null */): String

external fun execFileSync(command: String, args: ReadonlyArray<String>? = definedExternally /* null */, options: ExecFileSyncOptionsWithBufferEncoding? = definedExternally /* null */): Buffer

external fun execFileSync(command: String, args: ReadonlyArray<String>? = definedExternally /* null */, options: ExecFileSyncOptions? = definedExternally /* null */): Buffer