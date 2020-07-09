@file:JsQualifier("inspector.Profiler")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package inspector.Profiler

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

external interface ProfileNode {
    var id: Number
    var callFrame: Runtime.CallFrame
    var hitCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var children: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
    var deoptReason: String?
        get() = definedExternally
        set(value) = definedExternally
    var positionTicks: Array<PositionTickInfo>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Profile {
    var nodes: Array<ProfileNode>
    var startTime: Number
    var endTime: Number
    var samples: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
    var timeDeltas: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface PositionTickInfo {
    var line: Number
    var ticks: Number
}

external interface CoverageRange {
    var startOffset: Number
    var endOffset: Number
    var count: Number
}

external interface FunctionCoverage {
    var functionName: String
    var ranges: Array<CoverageRange>
    var isBlockCoverage: Boolean
}

external interface ScriptCoverage {
    var scriptId: Runtime.ScriptId
    var url: String
    var functions: Array<FunctionCoverage>
}

external interface TypeObject {
    var name: String
}

external interface TypeProfileEntry {
    var offset: Number
    var types: Array<TypeObject>
}

external interface ScriptTypeProfile {
    var scriptId: Runtime.ScriptId
    var url: String
    var entries: Array<TypeProfileEntry>
}

external interface SetSamplingIntervalParameterType {
    var interval: Number
}

external interface StartPreciseCoverageParameterType {
    var callCount: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var detailed: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface StopReturnType {
    var profile: Profile
}

external interface TakePreciseCoverageReturnType {
    var result: Array<ScriptCoverage>
}

external interface GetBestEffortCoverageReturnType {
    var result: Array<ScriptCoverage>
}

external interface TakeTypeProfileReturnType {
    var result: Array<ScriptTypeProfile>
}

external interface ConsoleProfileStartedEventDataType {
    var id: String
    var location: Debugger.Location
    var title: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConsoleProfileFinishedEventDataType {
    var id: String
    var location: Debugger.Location
    var profile: Profile
    var title: String?
        get() = definedExternally
        set(value) = definedExternally
}