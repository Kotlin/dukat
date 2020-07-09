@file:JsModule("os")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package os

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

external interface `T$0` {
    var user: Number
    var nice: Number
    var sys: Number
    var idle: Number
    var irq: Number
}

external interface CpuInfo {
    var model: String
    var speed: Number
    var times: `T$0`
}

external interface NetworkInterfaceBase {
    var address: String
    var netmask: String
    var mac: String
    var internal: Boolean
    var cidr: String?
}

external interface NetworkInterfaceInfoIPv4 : NetworkInterfaceBase {
    var family: String /* "IPv4" */
}

external interface NetworkInterfaceInfoIPv6 : NetworkInterfaceBase {
    var family: String /* "IPv6" */
    var scopeid: Number
}

external interface UserInfo<T> {
    var username: T
    var uid: Number
    var gid: Number
    var shell: T
    var homedir: T
}

external fun hostname(): String

external fun loadavg(): Array<Number>

external fun uptime(): Number

external fun freemem(): Number

external fun totalmem(): Number

external fun cpus(): Array<CpuInfo>

external fun type(): String

external fun release(): String

external interface `T$1` {
    @nativeGetter
    operator fun get(index: String): Array<dynamic /* NetworkInterfaceInfoIPv4 | NetworkInterfaceInfoIPv6 */>?
    @nativeSetter
    operator fun set(index: String, value: Array<dynamic /* NetworkInterfaceInfoIPv4 | NetworkInterfaceInfoIPv6 */>)
}

external fun networkInterfaces(): `T$1`

external fun homedir(): String

external interface `T$2` {
    var encoding: String /* 'buffer' */
}

external fun userInfo(options: `T$2`): UserInfo<Buffer>

external interface `T$3` {
    var encoding: String
}

external fun userInfo(options: `T$3`? = definedExternally /* null */): UserInfo<String>

external interface `T$4` {
    var SIGHUP: Number
    var SIGINT: Number
    var SIGQUIT: Number
    var SIGILL: Number
    var SIGTRAP: Number
    var SIGABRT: Number
    var SIGIOT: Number
    var SIGBUS: Number
    var SIGFPE: Number
    var SIGKILL: Number
    var SIGUSR1: Number
    var SIGSEGV: Number
    var SIGUSR2: Number
    var SIGPIPE: Number
    var SIGALRM: Number
    var SIGTERM: Number
    var SIGCHLD: Number
    var SIGSTKFLT: Number
    var SIGCONT: Number
    var SIGSTOP: Number
    var SIGTSTP: Number
    var SIGTTIN: Number
    var SIGTTOU: Number
    var SIGURG: Number
    var SIGXCPU: Number
    var SIGXFSZ: Number
    var SIGVTALRM: Number
    var SIGPROF: Number
    var SIGWINCH: Number
    var SIGIO: Number
    var SIGPOLL: Number
    var SIGPWR: Number
    var SIGSYS: Number
    var SIGUNUSED: Number
}

external interface `T$5` {
    var E2BIG: Number
    var EACCES: Number
    var EADDRINUSE: Number
    var EADDRNOTAVAIL: Number
    var EAFNOSUPPORT: Number
    var EAGAIN: Number
    var EALREADY: Number
    var EBADF: Number
    var EBADMSG: Number
    var EBUSY: Number
    var ECANCELED: Number
    var ECHILD: Number
    var ECONNABORTED: Number
    var ECONNREFUSED: Number
    var ECONNRESET: Number
    var EDEADLK: Number
    var EDESTADDRREQ: Number
    var EDOM: Number
    var EDQUOT: Number
    var EEXIST: Number
    var EFAULT: Number
    var EFBIG: Number
    var EHOSTUNREACH: Number
    var EIDRM: Number
    var EILSEQ: Number
    var EINPROGRESS: Number
    var EINTR: Number
    var EINVAL: Number
    var EIO: Number
    var EISCONN: Number
    var EISDIR: Number
    var ELOOP: Number
    var EMFILE: Number
    var EMLINK: Number
    var EMSGSIZE: Number
    var EMULTIHOP: Number
    var ENAMETOOLONG: Number
    var ENETDOWN: Number
    var ENETRESET: Number
    var ENETUNREACH: Number
    var ENFILE: Number
    var ENOBUFS: Number
    var ENODATA: Number
    var ENODEV: Number
    var ENOENT: Number
    var ENOEXEC: Number
    var ENOLCK: Number
    var ENOLINK: Number
    var ENOMEM: Number
    var ENOMSG: Number
    var ENOPROTOOPT: Number
    var ENOSPC: Number
    var ENOSR: Number
    var ENOSTR: Number
    var ENOSYS: Number
    var ENOTCONN: Number
    var ENOTDIR: Number
    var ENOTEMPTY: Number
    var ENOTSOCK: Number
    var ENOTSUP: Number
    var ENOTTY: Number
    var ENXIO: Number
    var EOPNOTSUPP: Number
    var EOVERFLOW: Number
    var EPERM: Number
    var EPIPE: Number
    var EPROTO: Number
    var EPROTONOSUPPORT: Number
    var EPROTOTYPE: Number
    var ERANGE: Number
    var EROFS: Number
    var ESPIPE: Number
    var ESRCH: Number
    var ESTALE: Number
    var ETIME: Number
    var ETIMEDOUT: Number
    var ETXTBSY: Number
    var EWOULDBLOCK: Number
    var EXDEV: Number
}

external interface `T$6` {
    var PRIORITY_LOW: Number
    var PRIORITY_BELOW_NORMAL: Number
    var PRIORITY_NORMAL: Number
    var PRIORITY_ABOVE_NORMAL: Number
    var PRIORITY_HIGH: Number
    var PRIORITY_HIGHEST: Number
}

external object constants {
    var UV_UDP_REUSEADDR: Number
    var signals: `T$4`
    var errno: `T$5`
    var priority: `T$6`
}

external fun arch(): String

external fun platform(): dynamic /* 'aix' | 'android' | 'darwin' | 'freebsd' | 'linux' | 'openbsd' | 'sunos' | 'win32' | 'cygwin' | 'netbsd' */

external fun tmpdir(): String

external var EOL: String

external fun endianness(): dynamic /* "BE" | "LE" */

external fun getPriority(pid: Number? = definedExternally /* null */): Number

external fun setPriority(priority: Number)

external fun setPriority(pid: Number, priority: Number)