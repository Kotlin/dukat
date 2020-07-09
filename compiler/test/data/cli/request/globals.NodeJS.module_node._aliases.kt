@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package NodeJS

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

typealias BeforeExitListener = (code: Number) -> Unit

typealias DisconnectListener = () -> Unit

typealias ExitListener = (code: Number) -> Unit

typealias RejectionHandledListener = (promise: Promise<Any>) -> Unit

typealias UncaughtExceptionListener = (error: Error) -> Unit

typealias UnhandledRejectionListener = (reason: Any?, promise: Promise<Any>) -> Unit

typealias WarningListener = (warning: Error) -> Unit

typealias MessageListener = (message: Any, sendHandle: Any) -> Unit

typealias SignalsListener = (signal: dynamic /* "SIGABRT" | "SIGALRM" | "SIGBUS" | "SIGCHLD" | "SIGCONT" | "SIGFPE" | "SIGHUP" | "SIGILL" | "SIGINT" | "SIGIO" | "SIGIOT" | "SIGKILL" | "SIGPIPE" | "SIGPOLL" | "SIGPROF" | "SIGPWR" | "SIGQUIT" | "SIGSEGV" | "SIGSTKFLT" | "SIGSTOP" | "SIGSYS" | "SIGTERM" | "SIGTRAP" | "SIGTSTP" | "SIGTTIN" | "SIGTTOU" | "SIGUNUSED" | "SIGURG" | "SIGUSR1" | "SIGUSR2" | "SIGVTALRM" | "SIGWINCH" | "SIGXCPU" | "SIGXFSZ" | "SIGBREAK" | "SIGLOST" | "SIGINFO" */) -> Unit

typealias NewListenerListener = (type: dynamic /* String | Any */, listener: (args: Array<Any>) -> Unit) -> Unit

typealias RemoveListenerListener = (type: dynamic /* String | Any */, listener: (args: Array<Any>) -> Unit) -> Unit

typealias MultipleResolveListener = (type: dynamic /* 'resolve' | 'reject' */, promise: Promise<Any>, value: Any) -> Unit