@file:JsQualifier("NodeJS")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package NodeJS

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

external interface InspectOptions {
    var getters: dynamic /* 'get' | 'set' | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var showHidden: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var depth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var colors: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var customInspect: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var showProxy: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var maxArrayLength: Number?
        get() = definedExternally
        set(value) = definedExternally
    var breakLength: Number?
        get() = definedExternally
        set(value) = definedExternally
    var compact: dynamic /* Boolean | Number */
        get() = definedExternally
        set(value) = definedExternally
    var sorted: dynamic /* Boolean | (a: String, b: String) -> Number */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConsoleConstructorOptions {
    var stdout: WritableStream
    var stderr: WritableStream?
        get() = definedExternally
        set(value) = definedExternally
    var ignoreErrors: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var colorMode: dynamic /* Boolean | 'auto' */
        get() = definedExternally
        set(value) = definedExternally
    var inspectOptions: InspectOptions?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConsoleConstructor {
    var prototype: Console
}

external interface CallSite {
    fun getThis(): Any
    fun getTypeName(): String?
    fun getFunction(): Function<*>?
    fun getFunctionName(): String?
    fun getMethodName(): String?
    fun getFileName(): String?
    fun getLineNumber(): Number?
    fun getColumnNumber(): Number?
    fun getEvalOrigin(): String?
    fun isToplevel(): Boolean
    fun isEval(): Boolean
    fun isNative(): Boolean
    fun isConstructor(): Boolean
}

external interface ErrnoException : Error {
    var errno: Number?
        get() = definedExternally
        set(value) = definedExternally
    var code: String?
        get() = definedExternally
        set(value) = definedExternally
    var path: String?
        get() = definedExternally
        set(value) = definedExternally
    var syscall: String?
        get() = definedExternally
        set(value) = definedExternally
    override var stack: String?
        get() = definedExternally
        set(value) = definedExternally
}

external open class EventEmitter {
    open fun addListener(event: String, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun addListener(event: Any, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun on(event: String, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun on(event: Any, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun once(event: String, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun once(event: Any, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun removeListener(event: String, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun removeListener(event: Any, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun off(event: String, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun off(event: Any, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun removeAllListeners(event: String? = definedExternally /* null */): EventEmitter /* this */
    open fun removeAllListeners(event: Any? = definedExternally /* null */): EventEmitter /* this */
    open fun setMaxListeners(n: Number): EventEmitter /* this */
    open fun getMaxListeners(): Number
    open fun listeners(event: String): Array<Function<*>>
    open fun listeners(event: Any): Array<Function<*>>
    open fun rawListeners(event: String): Array<Function<*>>
    open fun rawListeners(event: Any): Array<Function<*>>
    open fun emit(event: String, vararg args: Any): Boolean
    open fun emit(event: Any, vararg args: Any): Boolean
    open fun listenerCount(type: String): Number
    open fun listenerCount(type: Any): Number
    open fun prependListener(event: String, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun prependListener(event: Any, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun prependOnceListener(event: String, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun prependOnceListener(event: Any, listener: (args: Array<Any>) -> Unit): EventEmitter /* this */
    open fun eventNames(): Array<dynamic /* String | Any */>
    open fun removeAllListeners(): EventEmitter /* this */
}

external interface `T$2` {
    var end: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ReadableStream : EventEmitter {
    var readable: Boolean
    fun read(size: Number? = definedExternally /* null */): dynamic /* String | Buffer */
    fun setEncoding(encoding: String): ReadableStream /* this */
    fun pause(): ReadableStream /* this */
    fun resume(): ReadableStream /* this */
    fun isPaused(): Boolean
    fun <T : WritableStream> pipe(destination: T, options: `T$2`? = definedExternally /* null */): T
    fun unpipe(destination: WritableStream? = definedExternally /* null */): ReadableStream /* this */
    fun unshift(chunk: String, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */)
    fun unshift(chunk: Uint8Array, encoding: dynamic /* "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" */ = definedExternally /* null */)
    fun wrap(oldStream: ReadableStream): ReadableStream /* this */
}

external interface WritableStream : EventEmitter {
    var writable: Boolean
    fun write(buffer: Uint8Array, cb: ((err: Error?) -> Unit)? = definedExternally /* null */): Boolean
    fun write(buffer: String, cb: ((err: Error?) -> Unit)? = definedExternally /* null */): Boolean
    fun write(str: String, encoding: String? = definedExternally /* null */, cb: ((err: Error?) -> Unit)? = definedExternally /* null */): Boolean
    fun end(cb: (() -> Unit)? = definedExternally /* null */)
    fun end(data: String, cb: (() -> Unit)? = definedExternally /* null */)
    fun end(data: Uint8Array, cb: (() -> Unit)? = definedExternally /* null */)
    fun end(str: String, encoding: String? = definedExternally /* null */, cb: (() -> Unit)? = definedExternally /* null */)
}

external interface ReadWriteStream : ReadableStream, WritableStream

external interface Domain : EventEmitter {
    fun <T> run(fn: (args: Array<Any>) -> T, vararg args: Any): T
    fun add(emitter: EventEmitter)
    fun add(emitter: Timer)
    fun remove(emitter: EventEmitter)
    fun remove(emitter: Timer)
    fun <T : Function<*>> bind(cb: T): T
    fun <T : Function<*>> intercept(cb: T): T
    fun addListener(event: String, listener: (args: Array<Any>) -> Unit): Domain /* this */
    fun on(event: String, listener: (args: Array<Any>) -> Unit): Domain /* this */
    fun once(event: String, listener: (args: Array<Any>) -> Unit): Domain /* this */
    fun removeListener(event: String, listener: (args: Array<Any>) -> Unit): Domain /* this */
    fun removeAllListeners(event: String? = definedExternally /* null */): Domain /* this */
}

external interface MemoryUsage {
    var rss: Number
    var heapTotal: Number
    var heapUsed: Number
    var external: Number
}

external interface CpuUsage {
    var user: Number
    var system: Number
}

external interface ProcessRelease {
    var name: String
    var sourceUrl: String?
        get() = definedExternally
        set(value) = definedExternally
    var headersUrl: String?
        get() = definedExternally
        set(value) = definedExternally
    var libUrl: String?
        get() = definedExternally
        set(value) = definedExternally
    var lts: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ProcessVersions {
    var http_parser: String
    var node: String
    var v8: String
    var ares: String
    var uv: String
    var zlib: String
    var modules: String
    var openssl: String
}

external interface Socket : ReadWriteStream {
    var isTTY: String /* true */
}

external interface ProcessEnv {
    @nativeGetter
    operator fun get(key: String): String?
    @nativeSetter
    operator fun set(key: String, value: String?)
}

external interface HRTime {
    @nativeInvoke
    operator fun invoke(time: dynamic /* JsTuple<Number, Number> */ = definedExternally /* null */): dynamic /* JsTuple<Number, Number> */
}

external interface ProcessReport {
    var directory: String
    var filename: String
    fun getReport(err: Error? = definedExternally /* null */): String
    var reportOnFatalError: Boolean
    var reportOnSignal: Boolean
    var reportOnUncaughtException: Boolean
    var signal: dynamic /* "SIGABRT" | "SIGALRM" | "SIGBUS" | "SIGCHLD" | "SIGCONT" | "SIGFPE" | "SIGHUP" | "SIGILL" | "SIGINT" | "SIGIO" | "SIGIOT" | "SIGKILL" | "SIGPIPE" | "SIGPOLL" | "SIGPROF" | "SIGPWR" | "SIGQUIT" | "SIGSEGV" | "SIGSTKFLT" | "SIGSTOP" | "SIGSYS" | "SIGTERM" | "SIGTRAP" | "SIGTSTP" | "SIGTTIN" | "SIGTTOU" | "SIGUNUSED" | "SIGURG" | "SIGUSR1" | "SIGUSR2" | "SIGVTALRM" | "SIGWINCH" | "SIGXCPU" | "SIGXFSZ" | "SIGBREAK" | "SIGLOST" | "SIGINFO" */
        get() = definedExternally
        set(value) = definedExternally
    fun writeReport(fileName: String? = definedExternally /* null */): String
    fun writeReport(error: Error? = definedExternally /* null */): String
    fun writeReport(fileName: String? = definedExternally /* null */, err: Error? = definedExternally /* null */): String
}

external interface ResourceUsage {
    var fsRead: Number
    var fsWrite: Number
    var involuntaryContextSwitches: Number
    var ipcReceived: Number
    var ipcSent: Number
    var majorPageFault: Number
    var maxRSS: Number
    var minorPageFault: Number
    var sharedMemorySize: Number
    var signalsCount: Number
    var swappedOut: Number
    var systemCPUTime: Number
    var unsharedDataSize: Number
    var unsharedStackSize: Number
    var userCPUTime: Number
    var voluntaryContextSwitches: Number
}

external interface `T$3` {
    var cflags: Array<Any>
    var default_configuration: String
    var defines: Array<String>
    var include_dirs: Array<String>
    var libraries: Array<String>
}

external interface `T$4` {
    var clang: Number
    var host_arch: String
    var node_install_npm: Boolean
    var node_install_waf: Boolean
    var node_prefix: String
    var node_shared_openssl: Boolean
    var node_shared_v8: Boolean
    var node_shared_zlib: Boolean
    var node_use_dtrace: Boolean
    var node_use_etw: Boolean
    var node_use_openssl: Boolean
    var target_arch: String
    var v8_no_strict_aliasing: Number
    var v8_use_snapshot: Boolean
    var visibility: String
}

external interface `T$5` {
    var target_defaults: `T$3`
    var variables: `T$4`
}

external interface `T$6` {
    var inspector: Boolean
    var debug: Boolean
    var uv: Boolean
    var ipv6: Boolean
    var tls_alpn: Boolean
    var tls_sni: Boolean
    var tls_ocsp: Boolean
    var tls: Boolean
}

external interface `T$7` {
    var swallowErrors: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Process : EventEmitter {
    var stdout: WriteStream
    var stderr: WriteStream
    var stdin: ReadStream
    fun openStdin(): Socket
    var argv: Array<String>
    var argv0: String
    var execArgv: Array<String>
    var execPath: String
    fun abort()
    fun chdir(directory: String)
    fun cwd(): String
    var debugPort: Number
    fun emitWarning(warning: String, name: String? = definedExternally /* null */, ctor: Function<*>? = definedExternally /* null */)
    fun emitWarning(warning: Error, name: String? = definedExternally /* null */, ctor: Function<*>? = definedExternally /* null */)
    var env: ProcessEnv
    fun exit(code: Number? = definedExternally /* null */): Any
    var exitCode: Number?
        get() = definedExternally
        set(value) = definedExternally
    fun getgid(): Number
    fun setgid(id: Number)
    fun setgid(id: String)
    fun getuid(): Number
    fun setuid(id: Number)
    fun setuid(id: String)
    fun geteuid(): Number
    fun seteuid(id: Number)
    fun seteuid(id: String)
    fun getegid(): Number
    fun setegid(id: Number)
    fun setegid(id: String)
    fun getgroups(): Array<Number>
    fun setgroups(groups: Array<dynamic /* String | Number */>)
    fun setUncaughtExceptionCaptureCallback(cb: ((err: Error) -> Unit)?)
    fun hasUncaughtExceptionCaptureCallback(): Boolean
    var version: String
    var versions: ProcessVersions
    var config: `T$5`
    fun kill(pid: Number, signal: String? = definedExternally /* null */)
    fun kill(pid: Number, signal: Number? = definedExternally /* null */)
    var pid: Number
    var ppid: Number
    var title: String
    var arch: String
    var platform: dynamic /* 'aix' | 'android' | 'darwin' | 'freebsd' | 'linux' | 'openbsd' | 'sunos' | 'win32' | 'cygwin' | 'netbsd' */
        get() = definedExternally
        set(value) = definedExternally
    var mainModule: NodeModule?
        get() = definedExternally
        set(value) = definedExternally
    fun memoryUsage(): MemoryUsage
    fun cpuUsage(previousValue: CpuUsage? = definedExternally /* null */): CpuUsage
    fun nextTick(callback: Function<*>, vararg args: Any)
    var release: ProcessRelease
    var features: `T$6`
    fun umask(mask: Number? = definedExternally /* null */): Number
    fun uptime(): Number
    var hrtime: HRTime
    var domain: Domain
    val send: ((message: Any, sendHandle: Any? /* = null */, options: `T$7`?, callback: ((error: Error?) -> Unit)? /* = null */) -> Boolean)?
        get() = definedExternally
    fun disconnect()
    var connected: Boolean
    var allowedNodeEnvironmentFlags: ReadonlySet<String>
    var report: ProcessReport?
        get() = definedExternally
        set(value) = definedExternally
    fun resourceUsage(): ResourceUsage
    fun addListener(event: String /* "beforeExit" */, listener: BeforeExitListener): Process /* this */
    fun addListener(event: String /* "disconnect" */, listener: DisconnectListener): Process /* this */
    fun addListener(event: String /* "exit" */, listener: ExitListener): Process /* this */
    fun addListener(event: String /* "rejectionHandled" */, listener: RejectionHandledListener): Process /* this */
    fun addListener(event: String /* "uncaughtException" */, listener: UncaughtExceptionListener): Process /* this */
    fun addListener(event: String /* "unhandledRejection" */, listener: UnhandledRejectionListener): Process /* this */
    fun addListener(event: String /* "warning" */, listener: WarningListener): Process /* this */
    fun addListener(event: String /* "message" */, listener: MessageListener): Process /* this */
    fun addListener(event: dynamic /* "SIGABRT" | "SIGALRM" | "SIGBUS" | "SIGCHLD" | "SIGCONT" | "SIGFPE" | "SIGHUP" | "SIGILL" | "SIGINT" | "SIGIO" | "SIGIOT" | "SIGKILL" | "SIGPIPE" | "SIGPOLL" | "SIGPROF" | "SIGPWR" | "SIGQUIT" | "SIGSEGV" | "SIGSTKFLT" | "SIGSTOP" | "SIGSYS" | "SIGTERM" | "SIGTRAP" | "SIGTSTP" | "SIGTTIN" | "SIGTTOU" | "SIGUNUSED" | "SIGURG" | "SIGUSR1" | "SIGUSR2" | "SIGVTALRM" | "SIGWINCH" | "SIGXCPU" | "SIGXFSZ" | "SIGBREAK" | "SIGLOST" | "SIGINFO" */, listener: SignalsListener): Process /* this */
    fun addListener(event: String /* "newListener" */, listener: NewListenerListener): Process /* this */
    fun addListener(event: String /* "removeListener" */, listener: RemoveListenerListener): Process /* this */
    fun addListener(event: String /* "multipleResolves" */, listener: MultipleResolveListener): Process /* this */
    fun emit(event: String /* "beforeExit" */, code: Number): Boolean
    fun emit(event: String /* "disconnect" */): Boolean
    fun emit(event: String /* "exit" */, code: Number): Boolean
    fun emit(event: String /* "rejectionHandled" */, promise: Promise<Any>): Boolean
    fun emit(event: String /* "uncaughtException" */, error: Error): Boolean
    fun emit(event: String /* "unhandledRejection" */, reason: Any, promise: Promise<Any>): Boolean
    fun emit(event: String /* "warning" */, warning: Error): Boolean
    fun emit(event: String /* "message" */, message: Any, sendHandle: Any): Process /* this */
    fun emit(event: dynamic /* "SIGABRT" | "SIGALRM" | "SIGBUS" | "SIGCHLD" | "SIGCONT" | "SIGFPE" | "SIGHUP" | "SIGILL" | "SIGINT" | "SIGIO" | "SIGIOT" | "SIGKILL" | "SIGPIPE" | "SIGPOLL" | "SIGPROF" | "SIGPWR" | "SIGQUIT" | "SIGSEGV" | "SIGSTKFLT" | "SIGSTOP" | "SIGSYS" | "SIGTERM" | "SIGTRAP" | "SIGTSTP" | "SIGTTIN" | "SIGTTOU" | "SIGUNUSED" | "SIGURG" | "SIGUSR1" | "SIGUSR2" | "SIGVTALRM" | "SIGWINCH" | "SIGXCPU" | "SIGXFSZ" | "SIGBREAK" | "SIGLOST" | "SIGINFO" */, signal: dynamic /* "SIGABRT" | "SIGALRM" | "SIGBUS" | "SIGCHLD" | "SIGCONT" | "SIGFPE" | "SIGHUP" | "SIGILL" | "SIGINT" | "SIGIO" | "SIGIOT" | "SIGKILL" | "SIGPIPE" | "SIGPOLL" | "SIGPROF" | "SIGPWR" | "SIGQUIT" | "SIGSEGV" | "SIGSTKFLT" | "SIGSTOP" | "SIGSYS" | "SIGTERM" | "SIGTRAP" | "SIGTSTP" | "SIGTTIN" | "SIGTTOU" | "SIGUNUSED" | "SIGURG" | "SIGUSR1" | "SIGUSR2" | "SIGVTALRM" | "SIGWINCH" | "SIGXCPU" | "SIGXFSZ" | "SIGBREAK" | "SIGLOST" | "SIGINFO" */): Boolean
    fun emit(event: String /* "newListener" */, eventName: String, listener: (args: Array<Any>) -> Unit): Process /* this */
    fun emit(event: String /* "newListener" */, eventName: Any, listener: (args: Array<Any>) -> Unit): Process /* this */
    fun emit(event: String /* "removeListener" */, eventName: String, listener: (args: Array<Any>) -> Unit): Process /* this */
    fun emit(event: String /* "multipleResolves" */, listener: MultipleResolveListener): Process /* this */
    fun on(event: String /* "beforeExit" */, listener: BeforeExitListener): Process /* this */
    fun on(event: String /* "disconnect" */, listener: DisconnectListener): Process /* this */
    fun on(event: String /* "exit" */, listener: ExitListener): Process /* this */
    fun on(event: String /* "rejectionHandled" */, listener: RejectionHandledListener): Process /* this */
    fun on(event: String /* "uncaughtException" */, listener: UncaughtExceptionListener): Process /* this */
    fun on(event: String /* "unhandledRejection" */, listener: UnhandledRejectionListener): Process /* this */
    fun on(event: String /* "warning" */, listener: WarningListener): Process /* this */
    fun on(event: String /* "message" */, listener: MessageListener): Process /* this */
    fun on(event: dynamic /* "SIGABRT" | "SIGALRM" | "SIGBUS" | "SIGCHLD" | "SIGCONT" | "SIGFPE" | "SIGHUP" | "SIGILL" | "SIGINT" | "SIGIO" | "SIGIOT" | "SIGKILL" | "SIGPIPE" | "SIGPOLL" | "SIGPROF" | "SIGPWR" | "SIGQUIT" | "SIGSEGV" | "SIGSTKFLT" | "SIGSTOP" | "SIGSYS" | "SIGTERM" | "SIGTRAP" | "SIGTSTP" | "SIGTTIN" | "SIGTTOU" | "SIGUNUSED" | "SIGURG" | "SIGUSR1" | "SIGUSR2" | "SIGVTALRM" | "SIGWINCH" | "SIGXCPU" | "SIGXFSZ" | "SIGBREAK" | "SIGLOST" | "SIGINFO" */, listener: SignalsListener): Process /* this */
    fun on(event: String /* "newListener" */, listener: NewListenerListener): Process /* this */
    fun on(event: String /* "removeListener" */, listener: RemoveListenerListener): Process /* this */
    fun on(event: String /* "multipleResolves" */, listener: MultipleResolveListener): Process /* this */
    fun once(event: String /* "beforeExit" */, listener: BeforeExitListener): Process /* this */
    fun once(event: String /* "disconnect" */, listener: DisconnectListener): Process /* this */
    fun once(event: String /* "exit" */, listener: ExitListener): Process /* this */
    fun once(event: String /* "rejectionHandled" */, listener: RejectionHandledListener): Process /* this */
    fun once(event: String /* "uncaughtException" */, listener: UncaughtExceptionListener): Process /* this */
    fun once(event: String /* "unhandledRejection" */, listener: UnhandledRejectionListener): Process /* this */
    fun once(event: String /* "warning" */, listener: WarningListener): Process /* this */
    fun once(event: String /* "message" */, listener: MessageListener): Process /* this */
    fun once(event: dynamic /* "SIGABRT" | "SIGALRM" | "SIGBUS" | "SIGCHLD" | "SIGCONT" | "SIGFPE" | "SIGHUP" | "SIGILL" | "SIGINT" | "SIGIO" | "SIGIOT" | "SIGKILL" | "SIGPIPE" | "SIGPOLL" | "SIGPROF" | "SIGPWR" | "SIGQUIT" | "SIGSEGV" | "SIGSTKFLT" | "SIGSTOP" | "SIGSYS" | "SIGTERM" | "SIGTRAP" | "SIGTSTP" | "SIGTTIN" | "SIGTTOU" | "SIGUNUSED" | "SIGURG" | "SIGUSR1" | "SIGUSR2" | "SIGVTALRM" | "SIGWINCH" | "SIGXCPU" | "SIGXFSZ" | "SIGBREAK" | "SIGLOST" | "SIGINFO" */, listener: SignalsListener): Process /* this */
    fun once(event: String /* "newListener" */, listener: NewListenerListener): Process /* this */
    fun once(event: String /* "removeListener" */, listener: RemoveListenerListener): Process /* this */
    fun once(event: String /* "multipleResolves" */, listener: MultipleResolveListener): Process /* this */
    fun prependListener(event: String /* "beforeExit" */, listener: BeforeExitListener): Process /* this */
    fun prependListener(event: String /* "disconnect" */, listener: DisconnectListener): Process /* this */
    fun prependListener(event: String /* "exit" */, listener: ExitListener): Process /* this */
    fun prependListener(event: String /* "rejectionHandled" */, listener: RejectionHandledListener): Process /* this */
    fun prependListener(event: String /* "uncaughtException" */, listener: UncaughtExceptionListener): Process /* this */
    fun prependListener(event: String /* "unhandledRejection" */, listener: UnhandledRejectionListener): Process /* this */
    fun prependListener(event: String /* "warning" */, listener: WarningListener): Process /* this */
    fun prependListener(event: String /* "message" */, listener: MessageListener): Process /* this */
    fun prependListener(event: dynamic /* "SIGABRT" | "SIGALRM" | "SIGBUS" | "SIGCHLD" | "SIGCONT" | "SIGFPE" | "SIGHUP" | "SIGILL" | "SIGINT" | "SIGIO" | "SIGIOT" | "SIGKILL" | "SIGPIPE" | "SIGPOLL" | "SIGPROF" | "SIGPWR" | "SIGQUIT" | "SIGSEGV" | "SIGSTKFLT" | "SIGSTOP" | "SIGSYS" | "SIGTERM" | "SIGTRAP" | "SIGTSTP" | "SIGTTIN" | "SIGTTOU" | "SIGUNUSED" | "SIGURG" | "SIGUSR1" | "SIGUSR2" | "SIGVTALRM" | "SIGWINCH" | "SIGXCPU" | "SIGXFSZ" | "SIGBREAK" | "SIGLOST" | "SIGINFO" */, listener: SignalsListener): Process /* this */
    fun prependListener(event: String /* "newListener" */, listener: NewListenerListener): Process /* this */
    fun prependListener(event: String /* "removeListener" */, listener: RemoveListenerListener): Process /* this */
    fun prependListener(event: String /* "multipleResolves" */, listener: MultipleResolveListener): Process /* this */
    fun prependOnceListener(event: String /* "beforeExit" */, listener: BeforeExitListener): Process /* this */
    fun prependOnceListener(event: String /* "disconnect" */, listener: DisconnectListener): Process /* this */
    fun prependOnceListener(event: String /* "exit" */, listener: ExitListener): Process /* this */
    fun prependOnceListener(event: String /* "rejectionHandled" */, listener: RejectionHandledListener): Process /* this */
    fun prependOnceListener(event: String /* "uncaughtException" */, listener: UncaughtExceptionListener): Process /* this */
    fun prependOnceListener(event: String /* "unhandledRejection" */, listener: UnhandledRejectionListener): Process /* this */
    fun prependOnceListener(event: String /* "warning" */, listener: WarningListener): Process /* this */
    fun prependOnceListener(event: String /* "message" */, listener: MessageListener): Process /* this */
    fun prependOnceListener(event: dynamic /* "SIGABRT" | "SIGALRM" | "SIGBUS" | "SIGCHLD" | "SIGCONT" | "SIGFPE" | "SIGHUP" | "SIGILL" | "SIGINT" | "SIGIO" | "SIGIOT" | "SIGKILL" | "SIGPIPE" | "SIGPOLL" | "SIGPROF" | "SIGPWR" | "SIGQUIT" | "SIGSEGV" | "SIGSTKFLT" | "SIGSTOP" | "SIGSYS" | "SIGTERM" | "SIGTRAP" | "SIGTSTP" | "SIGTTIN" | "SIGTTOU" | "SIGUNUSED" | "SIGURG" | "SIGUSR1" | "SIGUSR2" | "SIGVTALRM" | "SIGWINCH" | "SIGXCPU" | "SIGXFSZ" | "SIGBREAK" | "SIGLOST" | "SIGINFO" */, listener: SignalsListener): Process /* this */
    fun prependOnceListener(event: String /* "newListener" */, listener: NewListenerListener): Process /* this */
    fun prependOnceListener(event: String /* "removeListener" */, listener: RemoveListenerListener): Process /* this */
    fun prependOnceListener(event: String /* "multipleResolves" */, listener: MultipleResolveListener): Process /* this */
    fun listeners(event: String /* "beforeExit" */): Array<BeforeExitListener>
    fun listeners(event: String /* "disconnect" */): Array<DisconnectListener>
    fun listeners(event: String /* "exit" */): Array<ExitListener>
    fun listeners(event: String /* "rejectionHandled" */): Array<RejectionHandledListener>
    fun listeners(event: String /* "uncaughtException" */): Array<UncaughtExceptionListener>
    fun listeners(event: String /* "unhandledRejection" */): Array<UnhandledRejectionListener>
    fun listeners(event: String /* "warning" */): Array<WarningListener>
    fun listeners(event: String /* "message" */): Array<MessageListener>
    fun listeners(event: dynamic /* "SIGABRT" | "SIGALRM" | "SIGBUS" | "SIGCHLD" | "SIGCONT" | "SIGFPE" | "SIGHUP" | "SIGILL" | "SIGINT" | "SIGIO" | "SIGIOT" | "SIGKILL" | "SIGPIPE" | "SIGPOLL" | "SIGPROF" | "SIGPWR" | "SIGQUIT" | "SIGSEGV" | "SIGSTKFLT" | "SIGSTOP" | "SIGSYS" | "SIGTERM" | "SIGTRAP" | "SIGTSTP" | "SIGTTIN" | "SIGTTOU" | "SIGUNUSED" | "SIGURG" | "SIGUSR1" | "SIGUSR2" | "SIGVTALRM" | "SIGWINCH" | "SIGXCPU" | "SIGXFSZ" | "SIGBREAK" | "SIGLOST" | "SIGINFO" */): Array<SignalsListener>
    fun listeners(event: String /* "newListener" */): Array<NewListenerListener>
    fun listeners(event: String /* "removeListener" */): Array<RemoveListenerListener>
    fun listeners(event: String /* "multipleResolves" */): Array<MultipleResolveListener>
    fun kill(pid: Number)
}

external interface Global {
    var Array: Any
    var ArrayBuffer: Any
    var Boolean: Any
    var Buffer: Any
    var DataView: Any
    var Date: Any
    var Error: Any
    var EvalError: Any
    var Float32Array: Any
    var Float64Array: Any
    var Function: Any
    var GLOBAL: Global
    var Infinity: Any
    var Int16Array: Any
    var Int32Array: Any
    var Int8Array: Any
    var Intl: Any
    var JSON: Any
    var Map: MapConstructor
    var Math: Any
    var NaN: Any
    var Number: Any
    var Object: Any
    var Promise: Function<*>
    var RangeError: Any
    var ReferenceError: Any
    var RegExp: Any
    var Set: SetConstructor
    var String: Any
    var Symbol: Function<*>
    var SyntaxError: Any
    var TypeError: Any
    var URIError: Any
    var Uint16Array: Any
    var Uint32Array: Any
    var Uint8Array: Any
    var Uint8ClampedArray: Function<*>
    var WeakMap: WeakMapConstructor
    var WeakSet: WeakSetConstructor
    var clearImmediate: (immediateId: Immediate) -> Unit
    var clearInterval: (intervalId: Timeout) -> Unit
    var clearTimeout: (timeoutId: Timeout) -> Unit
    var console: Any
    var decodeURI: Any
    var decodeURIComponent: Any
    var encodeURI: Any
    var encodeURIComponent: Any
    var escape: (str: String) -> String
    var eval: Any
    var global: Global
    var isFinite: Any
    var isNaN: Any
    var parseFloat: Any
    var parseInt: Any
    var process: Process
    var root: Global
    var setImmediate: (callback: (args: Array<Any>) -> Unit, args: Any) -> Immediate
    var setInterval: (callback: (args: Array<Any>) -> Unit, ms: Number, args: Any) -> Timeout
    var setTimeout: (callback: (args: Array<Any>) -> Unit, ms: Number, args: Any) -> Timeout
    var queueMicrotask: Any
    var undefined: Any
    var unescape: (str: String) -> String
    var gc: () -> Unit
    var v8debug: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Timer {
    fun hasRef(): Boolean
    fun ref(): Timer /* this */
    fun refresh(): Timer /* this */
    fun unref(): Timer /* this */
}

external open class Immediate {
    open fun hasRef(): Boolean
    open fun ref(): Immediate /* this */
    open fun unref(): Immediate /* this */
    open var _onImmediate: Function<*>
}

external open class Timeout : Timer {
    override fun hasRef(): Boolean
    override fun ref(): Timeout /* this */
    override fun refresh(): Timeout /* this */
    override fun unref(): Timeout /* this */
}

external open class Module(id: String, parent: Module? = definedExternally /* null */) {
    open var exports: Any
    open var require: NodeRequireFunction
    open var id: String
    open var filename: String
    open var loaded: Boolean
    open var parent: Module?
    open var children: Array<Module>
    open var paths: Array<String>

    companion object {
        fun runMain()
        fun wrap(code: String): String
        fun createRequireFromPath(path: String): NodeRequireFunction
        fun createRequire(path: String): NodeRequireFunction
        var builtinModules: Array<String>
        var Module: Any
    }
}