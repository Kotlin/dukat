@file:JsQualifier("inspector.Debugger")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package inspector.Debugger

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

external interface Location {
    var scriptId: Runtime.ScriptId
    var lineNumber: Number
    var columnNumber: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ScriptPosition {
    var lineNumber: Number
    var columnNumber: Number
}

external interface CallFrame {
    var callFrameId: CallFrameId
    var functionName: String
    var functionLocation: Location?
        get() = definedExternally
        set(value) = definedExternally
    var location: Location
    var url: String
    var scopeChain: Array<Scope>
    var this: Runtime.RemoteObject
    var returnValue: Runtime.RemoteObject?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Scope {
    var type: String
    var `object`: Runtime.RemoteObject
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var startLocation: Location?
        get() = definedExternally
        set(value) = definedExternally
    var endLocation: Location?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SearchMatch {
    var lineNumber: Number
    var lineContent: String
}

external interface BreakLocation {
    var scriptId: Runtime.ScriptId
    var lineNumber: Number
    var columnNumber: Number?
        get() = definedExternally
        set(value) = definedExternally
    var type: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SetBreakpointsActiveParameterType {
    var active: Boolean
}

external interface SetSkipAllPausesParameterType {
    var skip: Boolean
}

external interface SetBreakpointByUrlParameterType {
    var lineNumber: Number
    var url: String?
        get() = definedExternally
        set(value) = definedExternally
    var urlRegex: String?
        get() = definedExternally
        set(value) = definedExternally
    var scriptHash: String?
        get() = definedExternally
        set(value) = definedExternally
    var columnNumber: Number?
        get() = definedExternally
        set(value) = definedExternally
    var condition: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SetBreakpointParameterType {
    var location: Location
    var condition: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RemoveBreakpointParameterType {
    var breakpointId: BreakpointId
}

external interface GetPossibleBreakpointsParameterType {
    var start: Location
    var end: Location?
        get() = definedExternally
        set(value) = definedExternally
    var restrictToFunction: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ContinueToLocationParameterType {
    var location: Location
    var targetCallFrames: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PauseOnAsyncCallParameterType {
    var parentStackTraceId: Runtime.StackTraceId
}

external interface StepIntoParameterType {
    var breakOnAsyncCall: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface GetStackTraceParameterType {
    var stackTraceId: Runtime.StackTraceId
}

external interface SearchInContentParameterType {
    var scriptId: Runtime.ScriptId
    var query: String
    var caseSensitive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var isRegex: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SetScriptSourceParameterType {
    var scriptId: Runtime.ScriptId
    var scriptSource: String
    var dryRun: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RestartFrameParameterType {
    var callFrameId: CallFrameId
}

external interface GetScriptSourceParameterType {
    var scriptId: Runtime.ScriptId
}

external interface SetPauseOnExceptionsParameterType {
    var state: String
}

external interface EvaluateOnCallFrameParameterType {
    var callFrameId: CallFrameId
    var expression: String
    var objectGroup: String?
        get() = definedExternally
        set(value) = definedExternally
    var includeCommandLineAPI: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var silent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var returnByValue: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var generatePreview: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var throwOnSideEffect: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SetVariableValueParameterType {
    var scopeNumber: Number
    var variableName: String
    var newValue: Runtime.CallArgument
    var callFrameId: CallFrameId
}

external interface SetReturnValueParameterType {
    var newValue: Runtime.CallArgument
}

external interface SetAsyncCallStackDepthParameterType {
    var maxDepth: Number
}

external interface SetBlackboxPatternsParameterType {
    var patterns: Array<String>
}

external interface SetBlackboxedRangesParameterType {
    var scriptId: Runtime.ScriptId
    var positions: Array<ScriptPosition>
}

external interface EnableReturnType {
    var debuggerId: Runtime.UniqueDebuggerId
}

external interface SetBreakpointByUrlReturnType {
    var breakpointId: BreakpointId
    var locations: Array<Location>
}

external interface SetBreakpointReturnType {
    var breakpointId: BreakpointId
    var actualLocation: Location
}

external interface GetPossibleBreakpointsReturnType {
    var locations: Array<BreakLocation>
}

external interface GetStackTraceReturnType {
    var stackTrace: Runtime.StackTrace
}

external interface SearchInContentReturnType {
    var result: Array<SearchMatch>
}

external interface SetScriptSourceReturnType {
    var callFrames: Array<CallFrame>?
        get() = definedExternally
        set(value) = definedExternally
    var stackChanged: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var asyncStackTrace: Runtime.StackTrace?
        get() = definedExternally
        set(value) = definedExternally
    var asyncStackTraceId: Runtime.StackTraceId?
        get() = definedExternally
        set(value) = definedExternally
    var exceptionDetails: Runtime.ExceptionDetails?
        get() = definedExternally
        set(value) = definedExternally
}

external interface RestartFrameReturnType {
    var callFrames: Array<CallFrame>
    var asyncStackTrace: Runtime.StackTrace?
        get() = definedExternally
        set(value) = definedExternally
    var asyncStackTraceId: Runtime.StackTraceId?
        get() = definedExternally
        set(value) = definedExternally
}

external interface GetScriptSourceReturnType {
    var scriptSource: String
}

external interface EvaluateOnCallFrameReturnType {
    var result: Runtime.RemoteObject
    var exceptionDetails: Runtime.ExceptionDetails?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ScriptParsedEventDataType {
    var scriptId: Runtime.ScriptId
    var url: String
    var startLine: Number
    var startColumn: Number
    var endLine: Number
    var endColumn: Number
    var executionContextId: Runtime.ExecutionContextId
    var hash: String
    var executionContextAuxData: Any?
        get() = definedExternally
        set(value) = definedExternally
    var isLiveEdit: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var sourceMapURL: String?
        get() = definedExternally
        set(value) = definedExternally
    var hasSourceURL: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var isModule: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var length: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stackTrace: Runtime.StackTrace?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ScriptFailedToParseEventDataType {
    var scriptId: Runtime.ScriptId
    var url: String
    var startLine: Number
    var startColumn: Number
    var endLine: Number
    var endColumn: Number
    var executionContextId: Runtime.ExecutionContextId
    var hash: String
    var executionContextAuxData: Any?
        get() = definedExternally
        set(value) = definedExternally
    var sourceMapURL: String?
        get() = definedExternally
        set(value) = definedExternally
    var hasSourceURL: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var isModule: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var length: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stackTrace: Runtime.StackTrace?
        get() = definedExternally
        set(value) = definedExternally
}

external interface BreakpointResolvedEventDataType {
    var breakpointId: BreakpointId
    var location: Location
}

external interface PausedEventDataType {
    var callFrames: Array<CallFrame>
    var reason: String
    var data: Any?
        get() = definedExternally
        set(value) = definedExternally
    var hitBreakpoints: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var asyncStackTrace: Runtime.StackTrace?
        get() = definedExternally
        set(value) = definedExternally
    var asyncStackTraceId: Runtime.StackTraceId?
        get() = definedExternally
        set(value) = definedExternally
    var asyncCallStackTraceId: Runtime.StackTraceId?
        get() = definedExternally
        set(value) = definedExternally
}