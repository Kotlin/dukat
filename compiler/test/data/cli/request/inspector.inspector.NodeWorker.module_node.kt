@file:JsQualifier("inspector.NodeWorker")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package inspector.NodeWorker

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

external interface WorkerInfo {
    var workerId: WorkerID
    var type: String
    var title: String
    var url: String
}

external interface SendMessageToWorkerParameterType {
    var message: String
    var sessionId: SessionID
}

external interface EnableParameterType {
    var waitForDebuggerOnStart: Boolean
}

external interface DetachParameterType {
    var sessionId: SessionID
}

external interface AttachedToWorkerEventDataType {
    var sessionId: SessionID
    var workerInfo: WorkerInfo
    var waitingForDebugger: Boolean
}

external interface DetachedFromWorkerEventDataType {
    var sessionId: SessionID
}

external interface ReceivedMessageFromWorkerEventDataType {
    var sessionId: SessionID
    var message: String
}