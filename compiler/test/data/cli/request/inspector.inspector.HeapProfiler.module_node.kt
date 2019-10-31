@file:JsQualifier("inspector.HeapProfiler")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package inspector.HeapProfiler

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

external interface SamplingHeapProfileNode {
    var callFrame: Runtime.CallFrame
    var selfSize: Number
    var children: Array<SamplingHeapProfileNode>
}

external interface SamplingHeapProfile {
    var head: SamplingHeapProfileNode
}

external interface StartTrackingHeapObjectsParameterType {
    var trackAllocations: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface StopTrackingHeapObjectsParameterType {
    var reportProgress: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface TakeHeapSnapshotParameterType {
    var reportProgress: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface GetObjectByHeapObjectIdParameterType {
    var objectId: HeapSnapshotObjectId
    var objectGroup: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AddInspectedHeapObjectParameterType {
    var heapObjectId: HeapSnapshotObjectId
}

external interface GetHeapObjectIdParameterType {
    var objectId: Runtime.RemoteObjectId
}

external interface StartSamplingParameterType {
    var samplingInterval: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface GetObjectByHeapObjectIdReturnType {
    var result: Runtime.RemoteObject
}

external interface GetHeapObjectIdReturnType {
    var heapSnapshotObjectId: HeapSnapshotObjectId
}

external interface StopSamplingReturnType {
    var profile: SamplingHeapProfile
}

external interface GetSamplingProfileReturnType {
    var profile: SamplingHeapProfile
}

external interface AddHeapSnapshotChunkEventDataType {
    var chunk: String
}

external interface ReportHeapSnapshotProgressEventDataType {
    var done: Number
    var total: Number
    var finished: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface LastSeenObjectIdEventDataType {
    var lastSeenObjectId: Number
    var timestamp: Number
}

external interface HeapStatsUpdateEventDataType {
    var statsUpdate: Array<Number>
}