// --------- kotlin.js ---------
package kotlin.js


public external interface Console

public external class Date {
    open fun getDate(): Int
    open fun getDay(): Int
    open fun getFullYear(): Int
    open fun getHours(): Int
    open fun getMilliseconds(): Int
    open fun getMinutes(): Int
    open fun getMonth(): Int
    open fun getSeconds(): Int
    open fun getTime(): Double
    open fun getTimezoneOffset(): Int
    open fun getUTCDate(): Int
    open fun getUTCDay(): Int
    open fun getUTCFullYear(): Int
    open fun getUTCHours(): Int
    open fun getUTCMilliseconds(): Int
    open fun getUTCMinutes(): Int
    open fun getUTCMonth(): Int
    open fun getUTCSeconds(): Int
    open fun toDateString(): String
    open fun toISOString(): String
    open fun toJSON(): Json
    open fun toLocaleDateString(locales: Array, options: LocaleOptions): String
    open fun toLocaleDateString(locales: String, options: LocaleOptions): String
    open fun toLocaleString(locales: Array, options: LocaleOptions): String
    open fun toLocaleString(locales: String, options: LocaleOptions): String
    open fun toLocaleTimeString(locales: Array, options: LocaleOptions): String
    open fun toLocaleTimeString(locales: String, options: LocaleOptions): String
    open fun toTimeString(): String
    open fun toUTCString(): String
}

public external interface JsClass<T>

public external interface Json

public external open class Promise<T> {
    open fun catch(onRejected: ((Throwable) -> S)?): Promise
    open fun then(onFulfilled: ((T) -> S)?): Promise
    open fun then(onFulfilled: ((T) -> S)?, onRejected: ((Throwable) -> S)?): Promise
}

public external class RegExp {
    open val global: Boolean
    open val ignoreCase: Boolean
    open var lastIndex: Int
    open val multiline: Boolean
    open fun exec(str: String): RegExpMatch
    open fun test(str: String): Boolean
    open fun toString(): String
}

public external interface RegExpMatch
// --------- org.khronos.webgl ---------
package org.khronos.webgl


public external open class ArrayBuffer : BufferDataSource {
    open val byteLength: Int
    open fun slice(begin: Int, end: Int): ArrayBuffer
}

public external interface ArrayBufferView : BufferDataSource

public external interface BufferDataSource

public external open class DataView : ArrayBufferView {
    override val buffer: ArrayBuffer
    override val byteLength: Int
    override val byteOffset: Int
    open fun getFloat32(byteOffset: Int, littleEndian: Boolean): Float
    open fun getFloat64(byteOffset: Int, littleEndian: Boolean): Double
    open fun getInt16(byteOffset: Int, littleEndian: Boolean): Short
    open fun getInt32(byteOffset: Int, littleEndian: Boolean): Int
    open fun getInt8(byteOffset: Int): Byte
    open fun getUint16(byteOffset: Int, littleEndian: Boolean): Short
    open fun getUint32(byteOffset: Int, littleEndian: Boolean): Int
    open fun getUint8(byteOffset: Int): Byte
    open fun setFloat32(byteOffset: Int, value: Float, littleEndian: Boolean)
    open fun setFloat64(byteOffset: Int, value: Double, littleEndian: Boolean)
    open fun setInt16(byteOffset: Int, value: Short, littleEndian: Boolean)
    open fun setInt32(byteOffset: Int, value: Int, littleEndian: Boolean)
    open fun setInt8(byteOffset: Int, value: Byte)
    open fun setUint16(byteOffset: Int, value: Short, littleEndian: Boolean)
    open fun setUint32(byteOffset: Int, value: Int, littleEndian: Boolean)
    open fun setUint8(byteOffset: Int, value: Byte)
}

public external open class Float32Array : ArrayBufferView {
    override val buffer: ArrayBuffer
    override val byteLength: Int
    override val byteOffset: Int
    open val length: Int
    open fun set(array: Array, offset: Int)
    open fun set(array: Float32Array, offset: Int)
    open fun subarray(start: Int, end: Int): Float32Array
}

public external open class Float64Array : ArrayBufferView {
    override val buffer: ArrayBuffer
    override val byteLength: Int
    override val byteOffset: Int
    open val length: Int
    open fun set(array: Array, offset: Int)
    open fun set(array: Float64Array, offset: Int)
    open fun subarray(start: Int, end: Int): Float64Array
}

public external open class Int16Array : ArrayBufferView {
    override val buffer: ArrayBuffer
    override val byteLength: Int
    override val byteOffset: Int
    open val length: Int
    open fun set(array: Array, offset: Int)
    open fun set(array: Int16Array, offset: Int)
    open fun subarray(start: Int, end: Int): Int16Array
}

public external open class Int32Array : ArrayBufferView {
    override val buffer: ArrayBuffer
    override val byteLength: Int
    override val byteOffset: Int
    open val length: Int
    open fun set(array: Array, offset: Int)
    open fun set(array: Int32Array, offset: Int)
    open fun subarray(start: Int, end: Int): Int32Array
}

public external open class Int8Array : ArrayBufferView {
    override val buffer: ArrayBuffer
    override val byteLength: Int
    override val byteOffset: Int
    open val length: Int
    open fun set(array: Array, offset: Int)
    open fun set(array: Int8Array, offset: Int)
    open fun subarray(start: Int, end: Int): Int8Array
}

public external interface TexImageSource

public external open class Uint16Array : ArrayBufferView {
    override val buffer: ArrayBuffer
    override val byteLength: Int
    override val byteOffset: Int
    open val length: Int
    open fun set(array: Array, offset: Int)
    open fun set(array: Uint16Array, offset: Int)
    open fun subarray(start: Int, end: Int): Uint16Array
}

public external open class Uint32Array : ArrayBufferView {
    override val buffer: ArrayBuffer
    override val byteLength: Int
    override val byteOffset: Int
    open val length: Int
    open fun set(array: Array, offset: Int)
    open fun set(array: Uint32Array, offset: Int)
    open fun subarray(start: Int, end: Int): Uint32Array
}

public external open class Uint8Array : ArrayBufferView {
    override val buffer: ArrayBuffer
    override val byteLength: Int
    override val byteOffset: Int
    open val length: Int
    open fun set(array: Array, offset: Int)
    open fun set(array: Uint8Array, offset: Int)
    open fun subarray(start: Int, end: Int): Uint8Array
}

public external open class Uint8ClampedArray : ArrayBufferView {
    override val buffer: ArrayBuffer
    override val byteLength: Int
    override val byteOffset: Int
    open val length: Int
    open fun set(array: Array, offset: Int)
    open fun set(array: Uint8ClampedArray, offset: Int)
    open fun subarray(start: Int, end: Int): Uint8ClampedArray
}

public external abstract class WebGLActiveInfo {
    open val name: String
    open val size: Int
    open val type: Int
}

public external abstract class WebGLBuffer : WebGLObject

public external interface WebGLContextAttributes

public external open class WebGLContextEvent : Event {
    open val statusMessage: String
}

public external interface WebGLContextEventInit : EventInit

public external abstract class WebGLFramebuffer : WebGLObject

public external abstract class WebGLObject

public external abstract class WebGLProgram : WebGLObject

public external abstract class WebGLRenderbuffer : WebGLObject

public external abstract class WebGLRenderingContext : WebGLRenderingContextBase, RenderingContext

public external interface WebGLRenderingContextBase

public external abstract class WebGLShader : WebGLObject

public external abstract class WebGLShaderPrecisionFormat {
    open val precision: Int
    open val rangeMax: Int
    open val rangeMin: Int
}

public external abstract class WebGLTexture : WebGLObject

public external abstract class WebGLUniformLocation
// --------- org.w3c.dom ---------
package org.w3c.dom


public external interface AbstractWorker

public external interface AddEventListenerOptions : EventListenerOptions

public external abstract class ApplicationCache : EventTarget {
    open var oncached: ((Event) -> dynamic)?
    open var onchecking: ((Event) -> dynamic)?
    open var ondownloading: ((Event) -> dynamic)?
    open var onerror: ((Event) -> dynamic)?
    open var onnoupdate: ((Event) -> dynamic)?
    open var onobsolete: ((Event) -> dynamic)?
    open var onprogress: ((ProgressEvent) -> dynamic)?
    open var onupdateready: ((Event) -> dynamic)?
    open val status: Short
    open fun abort()
    open fun swapCache()
    open fun update()
}

public external interface AssignedNodesOptions

public external abstract class Attr : Node {
    open val localName: String
    open val name: String
    open val namespaceURI: String
    open val ownerElement: Element
    open val prefix: String
    open val specified: Boolean
    open var value: String
}

public external open class Audio : HTMLAudioElement {
    override val assignedSlot: HTMLSlotElement
    override val childElementCount: Int
    override val children: HTMLCollection
    override var contentEditable: String
    override val firstElementChild: Element
    override val isContentEditable: Boolean
    override val lastElementChild: Element
    override val nextElementSibling: Element
    override var onabort: ((Event) -> dynamic)?
    override var onblur: ((FocusEvent) -> dynamic)?
    override var oncancel: ((Event) -> dynamic)?
    override var oncanplay: ((Event) -> dynamic)?
    override var oncanplaythrough: ((Event) -> dynamic)?
    override var onchange: ((Event) -> dynamic)?
    override var onclick: ((MouseEvent) -> dynamic)?
    override var onclose: ((Event) -> dynamic)?
    override var oncontextmenu: ((MouseEvent) -> dynamic)?
    override var oncopy: ((ClipboardEvent) -> dynamic)?
    override var oncuechange: ((Event) -> dynamic)?
    override var oncut: ((ClipboardEvent) -> dynamic)?
    override var ondblclick: ((MouseEvent) -> dynamic)?
    override var ondrag: ((DragEvent) -> dynamic)?
    override var ondragend: ((DragEvent) -> dynamic)?
    override var ondragenter: ((DragEvent) -> dynamic)?
    override var ondragexit: ((DragEvent) -> dynamic)?
    override var ondragleave: ((DragEvent) -> dynamic)?
    override var ondragover: ((DragEvent) -> dynamic)?
    override var ondragstart: ((DragEvent) -> dynamic)?
    override var ondrop: ((DragEvent) -> dynamic)?
    override var ondurationchange: ((Event) -> dynamic)?
    override var onemptied: ((Event) -> dynamic)?
    override var onended: ((Event) -> dynamic)?
    override var onerror: Function5
    override var onfocus: ((FocusEvent) -> dynamic)?
    override var ongotpointercapture: ((PointerEvent) -> dynamic)?
    override var oninput: ((InputEvent) -> dynamic)?
    override var oninvalid: ((Event) -> dynamic)?
    override var onkeydown: ((KeyboardEvent) -> dynamic)?
    override var onkeypress: ((KeyboardEvent) -> dynamic)?
    override var onkeyup: ((KeyboardEvent) -> dynamic)?
    override var onload: ((Event) -> dynamic)?
    override var onloadeddata: ((Event) -> dynamic)?
    override var onloadedmetadata: ((Event) -> dynamic)?
    override var onloadend: ((Event) -> dynamic)?
    override var onloadstart: ((ProgressEvent) -> dynamic)?
    override var onlostpointercapture: ((PointerEvent) -> dynamic)?
    override var onmousedown: ((MouseEvent) -> dynamic)?
    override var onmouseenter: ((MouseEvent) -> dynamic)?
    override var onmouseleave: ((MouseEvent) -> dynamic)?
    override var onmousemove: ((MouseEvent) -> dynamic)?
    override var onmouseout: ((MouseEvent) -> dynamic)?
    override var onmouseover: ((MouseEvent) -> dynamic)?
    override var onmouseup: ((MouseEvent) -> dynamic)?
    override var onpaste: ((ClipboardEvent) -> dynamic)?
    override var onpause: ((Event) -> dynamic)?
    override var onplay: ((Event) -> dynamic)?
    override var onplaying: ((Event) -> dynamic)?
    override var onpointercancel: ((PointerEvent) -> dynamic)?
    override var onpointerdown: ((PointerEvent) -> dynamic)?
    override var onpointerenter: ((PointerEvent) -> dynamic)?
    override var onpointerleave: ((PointerEvent) -> dynamic)?
    override var onpointermove: ((PointerEvent) -> dynamic)?
    override var onpointerout: ((PointerEvent) -> dynamic)?
    override var onpointerover: ((PointerEvent) -> dynamic)?
    override var onpointerup: ((PointerEvent) -> dynamic)?
    override var onprogress: ((ProgressEvent) -> dynamic)?
    override var onratechange: ((Event) -> dynamic)?
    override var onreset: ((Event) -> dynamic)?
    override var onresize: ((Event) -> dynamic)?
    override var onscroll: ((Event) -> dynamic)?
    override var onseeked: ((Event) -> dynamic)?
    override var onseeking: ((Event) -> dynamic)?
    override var onselect: ((Event) -> dynamic)?
    override var onshow: ((Event) -> dynamic)?
    override var onstalled: ((Event) -> dynamic)?
    override var onsubmit: ((Event) -> dynamic)?
    override var onsuspend: ((Event) -> dynamic)?
    override var ontimeupdate: ((Event) -> dynamic)?
    override var ontoggle: ((Event) -> dynamic)?
    override var onvolumechange: ((Event) -> dynamic)?
    override var onwaiting: ((Event) -> dynamic)?
    override var onwheel: ((WheelEvent) -> dynamic)?
    override val previousElementSibling: Element
    override val style: CSSStyleDeclaration
    open fun after(nodes: Array)
    open fun append(nodes: Array)
    open fun before(nodes: Array)
    open fun convertPointFromNode(point: DOMPointInit, from: dynamic, options: ConvertCoordinateOptions): DOMPoint
    open fun convertQuadFromNode(quad: dynamic, from: dynamic, options: ConvertCoordinateOptions): DOMQuad
    open fun convertRectFromNode(rect: DOMRectReadOnly, from: dynamic, options: ConvertCoordinateOptions): DOMQuad
    open fun getBoxQuads(options: BoxQuadOptions): Array
    open fun prepend(nodes: Array)
    open fun querySelector(selectors: String): Element
    open fun querySelectorAll(selectors: String): NodeList
    open fun remove()
    open fun replaceWith(nodes: Array)
}

public external abstract class AudioTrack : UnionAudioTrackOrTextTrackOrVideoTrack {
    open var enabled: Boolean
    open val id: String
    open val kind: String
    open val label: String
    open val language: String
    open val sourceBuffer: SourceBuffer
}

public external abstract class AudioTrackList : EventTarget {
    open val length: Int
    open var onaddtrack: ((TrackEvent) -> dynamic)?
    open var onchange: ((Event) -> dynamic)?
    open var onremovetrack: ((TrackEvent) -> dynamic)?
    open fun getTrackById(id: String): AudioTrack
}

public external abstract class BarProp {
    open val visible: Boolean
}

public external open class BeforeUnloadEvent : Event {
    open var returnValue: String
}

public external interface BinaryType

public external interface BoxQuadOptions

public external open class BroadcastChannel : EventTarget {
    open val name: String
    open var onmessage: ((MessageEvent) -> dynamic)?
    open fun close()
    open fun postMessage(message: Any)
}

public external open class CDATASection : Text

public external interface CSSBoxType

public external interface CanPlayTypeResult

public external interface CanvasCompositing

public external interface CanvasDirection

public external interface CanvasDrawImage

public external interface CanvasDrawPath

public external interface CanvasFillRule

public external interface CanvasFillStrokeStyles

public external interface CanvasFilters

public external abstract class CanvasGradient {
    open fun addColorStop(offset: Double, color: String)
}

public external interface CanvasHitRegion

public external interface CanvasImageData

public external interface CanvasImageSmoothing

public external interface CanvasImageSource : ImageBitmapSource

public external interface CanvasLineCap

public external interface CanvasLineJoin

public external interface CanvasPath

public external interface CanvasPathDrawingStyles

public external abstract class CanvasPattern {
    open fun setTransform(transform: dynamic)
}

public external interface CanvasRect

public external abstract class CanvasRenderingContext2D : CanvasState, CanvasTransform, CanvasCompositing, CanvasImageSmoothing, CanvasFillStrokeStyles, CanvasShadowStyles, CanvasFilters, CanvasRect, CanvasDrawPath, CanvasUserInterface, CanvasText, CanvasDrawImage, CanvasHitRegion, CanvasImageData, CanvasPathDrawingStyles, CanvasTextDrawingStyles, CanvasPath, RenderingContext {
    open val canvas: HTMLCanvasElement
}

public external interface CanvasRenderingContext2DSettings

public external interface CanvasShadowStyles

public external interface CanvasState

public external interface CanvasText

public external interface CanvasTextAlign

public external interface CanvasTextBaseline

public external interface CanvasTextDrawingStyles

public external interface CanvasTransform

public external interface CanvasUserInterface

public external abstract class CaretPosition {
    open val offset: Int
    open val offsetNode: Node
    open fun getClientRect(): DOMRect
}

public external abstract class CharacterData : Node, NonDocumentTypeChildNode, ChildNode {
    open var data: String
    open val length: Int
    open fun appendData(data: String)
    open fun deleteData(offset: Int, count: Int)
    open fun insertData(offset: Int, data: String)
    open fun replaceData(offset: Int, count: Int, data: String)
    open fun substringData(offset: Int, count: Int): String
}

public external interface ChildNode

public external open class CloseEvent : Event {
    open val code: Short
    open val reason: String
    open val wasClean: Boolean
}

public external interface CloseEventInit : EventInit

public external interface ColorSpaceConversion

public external open class Comment : CharacterData {
    override val nextElementSibling: Element
    override val previousElementSibling: Element
    open fun after(nodes: Array)
    open fun before(nodes: Array)
    open fun remove()
    open fun replaceWith(nodes: Array)
}

public external interface ConvertCoordinateOptions

public external abstract class CustomElementRegistry {
    open fun define(name: String, constructor: Function0, options: ElementDefinitionOptions)
    open fun get(name: String): Any
    open fun whenDefined(name: String): Promise
}

public external open class CustomEvent : Event {
    open val detail: Any
    open fun initCustomEvent(type: String, bubbles: Boolean, cancelable: Boolean, detail: Any)
}

public external interface CustomEventInit : EventInit

public external abstract class DOMImplementation {
    open fun createDocument(namespace: String, qualifiedName: String, doctype: DocumentType): XMLDocument
    open fun createDocumentType(qualifiedName: String, publicId: String, systemId: String): DocumentType
    open fun createHTMLDocument(title: String): Document
    open fun hasFeature(): Boolean
}

public external open class DOMMatrix : DOMMatrixReadOnly {
    override var a: Double
    override var b: Double
    override var c: Double
    override var d: Double
    override var e: Double
    override var f: Double
    override var m11: Double
    override var m12: Double
    override var m13: Double
    override var m14: Double
    override var m21: Double
    override var m22: Double
    override var m23: Double
    override var m24: Double
    override var m31: Double
    override var m32: Double
    override var m33: Double
    override var m34: Double
    override var m41: Double
    override var m42: Double
    override var m43: Double
    override var m44: Double
    open fun invertSelf(): DOMMatrix
    open fun multiplySelf(other: DOMMatrix): DOMMatrix
    open fun preMultiplySelf(other: DOMMatrix): DOMMatrix
    open fun rotateAxisAngleSelf(x: Double, y: Double, z: Double, angle: Double): DOMMatrix
    open fun rotateFromVectorSelf(x: Double, y: Double): DOMMatrix
    open fun rotateSelf(angle: Double, originX: Double, originY: Double): DOMMatrix
    open fun scale3dSelf(scale: Double, originX: Double, originY: Double, originZ: Double): DOMMatrix
    open fun scaleNonUniformSelf(scaleX: Double, scaleY: Double, scaleZ: Double, originX: Double, originY: Double, originZ: Double): DOMMatrix
    open fun scaleSelf(scale: Double, originX: Double, originY: Double): DOMMatrix
    open fun setMatrixValue(transformList: String): DOMMatrix
    open fun skewXSelf(sx: Double): DOMMatrix
    open fun skewYSelf(sy: Double): DOMMatrix
    open fun translateSelf(tx: Double, ty: Double, tz: Double): DOMMatrix
}

public external open class DOMMatrixReadOnly {
    open val a: Double
    open val b: Double
    open val c: Double
    open val d: Double
    open val e: Double
    open val f: Double
    open val is2D: Boolean
    open val isIdentity: Boolean
    open val m11: Double
    open val m12: Double
    open val m13: Double
    open val m14: Double
    open val m21: Double
    open val m22: Double
    open val m23: Double
    open val m24: Double
    open val m31: Double
    open val m32: Double
    open val m33: Double
    open val m34: Double
    open val m41: Double
    open val m42: Double
    open val m43: Double
    open val m44: Double
    open fun flipX(): DOMMatrix
    open fun flipY(): DOMMatrix
    open fun inverse(): DOMMatrix
    open fun multiply(other: DOMMatrix): DOMMatrix
    open fun rotate(angle: Double, originX: Double, originY: Double): DOMMatrix
    open fun rotateAxisAngle(x: Double, y: Double, z: Double, angle: Double): DOMMatrix
    open fun rotateFromVector(x: Double, y: Double): DOMMatrix
    open fun scale(scale: Double, originX: Double, originY: Double): DOMMatrix
    open fun scale3d(scale: Double, originX: Double, originY: Double, originZ: Double): DOMMatrix
    open fun scaleNonUniform(scaleX: Double, scaleY: Double, scaleZ: Double, originX: Double, originY: Double, originZ: Double): DOMMatrix
    open fun skewX(sx: Double): DOMMatrix
    open fun skewY(sy: Double): DOMMatrix
    open fun toFloat32Array(): Float32Array
    open fun toFloat64Array(): Float64Array
    open fun transformPoint(point: DOMPointInit): DOMPoint
    open fun translate(tx: Double, ty: Double, tz: Double): DOMMatrix
}

public external open class DOMPoint : DOMPointReadOnly {
    override var w: Double
    override var x: Double
    override var y: Double
    override var z: Double
}

public external interface DOMPointInit

public external open class DOMPointReadOnly {
    open val w: Double
    open val x: Double
    open val y: Double
    open val z: Double
    open fun matrixTransform(matrix: DOMMatrixReadOnly): DOMPoint
}

public external open class DOMQuad {
    open val bounds: DOMRectReadOnly
    open val p1: DOMPoint
    open val p2: DOMPoint
    open val p3: DOMPoint
    open val p4: DOMPoint
}

public external open class DOMRect : DOMRectReadOnly {
    override var height: Double
    override var width: Double
    override var x: Double
    override var y: Double
}

public external interface DOMRectInit

public external interface DOMRectList : ItemArrayLike<DOMRect>

public external open class DOMRectReadOnly {
    open val bottom: Double
    open val height: Double
    open val left: Double
    open val right: Double
    open val top: Double
    open val width: Double
    open val x: Double
    open val y: Double
}

public external abstract class DOMStringMap

public external abstract class DOMTokenList : ItemArrayLike<String> {
    open var value: String
    open fun add(tokens: Array)
    open fun contains(token: String): Boolean
    open fun item(index: Int): String
    open fun remove(tokens: Array)
    open fun replace(token: String, newToken: String)
    open fun supports(token: String): Boolean
    open fun toggle(token: String, force: Boolean): Boolean
}

public external abstract class DataTransfer {
    open var dropEffect: String
    open var effectAllowed: String
    open val files: FileList
    open val items: DataTransferItemList
    open val types: Array
    open fun clearData(format: String)
    open fun getData(format: String): String
    open fun setData(format: String, data: String)
    open fun setDragImage(image: Element, x: Int, y: Int)
}

public external abstract class DataTransferItem {
    open val kind: String
    open val type: String
    open fun getAsFile(): File
    open fun getAsString(_callback: ((String) -> Unit)?)
}

public external abstract class DataTransferItemList {
    open val length: Int
    open fun add(data: String, type: String): DataTransferItem
    open fun add(data: File): DataTransferItem
    open fun clear()
    open fun remove(index: Int)
}

public external abstract class DedicatedWorkerGlobalScope : WorkerGlobalScope {
    open var onmessage: ((MessageEvent) -> dynamic)?
    open fun close()
    open fun postMessage(message: Any, transfer: Array)
}

public external open class Document : Node, GlobalEventHandlers, DocumentAndElementEventHandlers, NonElementParentNode, DocumentOrShadowRoot, ParentNode, GeometryUtils {
    open val URL: String
    open val activeElement: Element
    open var alinkColor: String
    open val all: HTMLAllCollection
    open val anchors: HTMLCollection
    open val applets: HTMLCollection
    open var bgColor: String
    open var body: HTMLElement
    open val characterSet: String
    open val charset: String
    override val childElementCount: Int
    override val children: HTMLCollection
    open val compatMode: String
    open val contentType: String
    open var cookie: String
    open val currentScript: HTMLOrSVGScriptElement
    open val defaultView: Window
    open var designMode: String
    open var dir: String
    open val doctype: DocumentType
    open val documentElement: Element
    open val documentURI: String
    open var domain: String
    open val embeds: HTMLCollection
    open var fgColor: String
    override val firstElementChild: Element
    open val forms: HTMLCollection
    open val fullscreen: Boolean
    override val fullscreenElement: Element
    open val fullscreenEnabled: Boolean
    open val head: HTMLHeadElement
    open val images: HTMLCollection
    open val implementation: DOMImplementation
    open val inputEncoding: String
    override val lastElementChild: Element
    open val lastModified: String
    open var linkColor: String
    open val links: HTMLCollection
    open val location: Location
    override var onabort: ((Event) -> dynamic)?
    override var onblur: ((FocusEvent) -> dynamic)?
    override var oncancel: ((Event) -> dynamic)?
    override var oncanplay: ((Event) -> dynamic)?
    override var oncanplaythrough: ((Event) -> dynamic)?
    override var onchange: ((Event) -> dynamic)?
    override var onclick: ((MouseEvent) -> dynamic)?
    override var onclose: ((Event) -> dynamic)?
    override var oncontextmenu: ((MouseEvent) -> dynamic)?
    override var oncopy: ((ClipboardEvent) -> dynamic)?
    override var oncuechange: ((Event) -> dynamic)?
    override var oncut: ((ClipboardEvent) -> dynamic)?
    override var ondblclick: ((MouseEvent) -> dynamic)?
    override var ondrag: ((DragEvent) -> dynamic)?
    override var ondragend: ((DragEvent) -> dynamic)?
    override var ondragenter: ((DragEvent) -> dynamic)?
    override var ondragexit: ((DragEvent) -> dynamic)?
    override var ondragleave: ((DragEvent) -> dynamic)?
    override var ondragover: ((DragEvent) -> dynamic)?
    override var ondragstart: ((DragEvent) -> dynamic)?
    override var ondrop: ((DragEvent) -> dynamic)?
    override var ondurationchange: ((Event) -> dynamic)?
    override var onemptied: ((Event) -> dynamic)?
    override var onended: ((Event) -> dynamic)?
    override var onerror: Function5
    override var onfocus: ((FocusEvent) -> dynamic)?
    open var onfullscreenchange: ((Event) -> dynamic)?
    open var onfullscreenerror: ((Event) -> dynamic)?
    override var ongotpointercapture: ((PointerEvent) -> dynamic)?
    override var oninput: ((InputEvent) -> dynamic)?
    override var oninvalid: ((Event) -> dynamic)?
    override var onkeydown: ((KeyboardEvent) -> dynamic)?
    override var onkeypress: ((KeyboardEvent) -> dynamic)?
    override var onkeyup: ((KeyboardEvent) -> dynamic)?
    override var onload: ((Event) -> dynamic)?
    override var onloadeddata: ((Event) -> dynamic)?
    override var onloadedmetadata: ((Event) -> dynamic)?
    override var onloadend: ((Event) -> dynamic)?
    override var onloadstart: ((ProgressEvent) -> dynamic)?
    override var onlostpointercapture: ((PointerEvent) -> dynamic)?
    override var onmousedown: ((MouseEvent) -> dynamic)?
    override var onmouseenter: ((MouseEvent) -> dynamic)?
    override var onmouseleave: ((MouseEvent) -> dynamic)?
    override var onmousemove: ((MouseEvent) -> dynamic)?
    override var onmouseout: ((MouseEvent) -> dynamic)?
    override var onmouseover: ((MouseEvent) -> dynamic)?
    override var onmouseup: ((MouseEvent) -> dynamic)?
    override var onpaste: ((ClipboardEvent) -> dynamic)?
    override var onpause: ((Event) -> dynamic)?
    override var onplay: ((Event) -> dynamic)?
    override var onplaying: ((Event) -> dynamic)?
    override var onpointercancel: ((PointerEvent) -> dynamic)?
    override var onpointerdown: ((PointerEvent) -> dynamic)?
    override var onpointerenter: ((PointerEvent) -> dynamic)?
    override var onpointerleave: ((PointerEvent) -> dynamic)?
    override var onpointermove: ((PointerEvent) -> dynamic)?
    override var onpointerout: ((PointerEvent) -> dynamic)?
    override var onpointerover: ((PointerEvent) -> dynamic)?
    override var onpointerup: ((PointerEvent) -> dynamic)?
    override var onprogress: ((ProgressEvent) -> dynamic)?
    override var onratechange: ((Event) -> dynamic)?
    open var onreadystatechange: ((Event) -> dynamic)?
    override var onreset: ((Event) -> dynamic)?
    override var onresize: ((Event) -> dynamic)?
    override var onscroll: ((Event) -> dynamic)?
    override var onseeked: ((Event) -> dynamic)?
    override var onseeking: ((Event) -> dynamic)?
    override var onselect: ((Event) -> dynamic)?
    override var onshow: ((Event) -> dynamic)?
    override var onstalled: ((Event) -> dynamic)?
    override var onsubmit: ((Event) -> dynamic)?
    override var onsuspend: ((Event) -> dynamic)?
    override var ontimeupdate: ((Event) -> dynamic)?
    override var ontoggle: ((Event) -> dynamic)?
    override var onvolumechange: ((Event) -> dynamic)?
    override var onwaiting: ((Event) -> dynamic)?
    override var onwheel: ((WheelEvent) -> dynamic)?
    open val origin: String
    open val plugins: HTMLCollection
    open val readyState: DocumentReadyState
    open val referrer: String
    open val rootElement: SVGSVGElement
    open val scripts: HTMLCollection
    open val scrollingElement: Element
    open val styleSheets: StyleSheetList
    open var title: String
    open var vlinkColor: String
    open fun adoptNode(node: Node): Node
    open fun append(nodes: Array)
    open fun captureEvents()
    open fun caretPositionFromPoint(x: Double, y: Double): CaretPosition
    open fun clear()
    open fun close()
    open fun convertPointFromNode(point: DOMPointInit, from: dynamic, options: ConvertCoordinateOptions): DOMPoint
    open fun convertQuadFromNode(quad: dynamic, from: dynamic, options: ConvertCoordinateOptions): DOMQuad
    open fun convertRectFromNode(rect: DOMRectReadOnly, from: dynamic, options: ConvertCoordinateOptions): DOMQuad
    open fun createAttribute(localName: String): Attr
    open fun createAttributeNS(namespace: String, qualifiedName: String): Attr
    open fun createCDATASection(data: String): CDATASection
    open fun createComment(data: String): Comment
    open fun createDocumentFragment(): DocumentFragment
    open fun createElement(localName: String, options: ElementCreationOptions): Element
    open fun createElementNS(namespace: String, qualifiedName: String, options: ElementCreationOptions): Element
    open fun createEvent(interface: String): Event
    open fun createNodeIterator(root: Node, whatToShow: Int, filter: ((Node) -> Short)?): NodeIterator
    open fun createNodeIterator(root: Node, whatToShow: Int, filter: NodeFilter): NodeIterator
    open fun createProcessingInstruction(target: String, data: String): ProcessingInstruction
    open fun createRange(): Range
    open fun createTextNode(data: String): Text
    open fun createTouch(view: Window, target: EventTarget, identifier: Int, pageX: Int, pageY: Int, screenX: Int, screenY: Int): Touch
    open fun createTouchList(touches: Array): TouchList
    open fun createTreeWalker(root: Node, whatToShow: Int, filter: ((Node) -> Short)?): TreeWalker
    open fun createTreeWalker(root: Node, whatToShow: Int, filter: NodeFilter): TreeWalker
    open fun elementFromPoint(x: Double, y: Double): Element
    open fun elementsFromPoint(x: Double, y: Double): Array
    open fun execCommand(commandId: String, showUI: Boolean, value: String): Boolean
    open fun exitFullscreen(): Promise
    open fun getBoxQuads(options: BoxQuadOptions): Array
    open fun getElementById(elementId: String): Element
    open fun getElementsByClassName(classNames: String): HTMLCollection
    open fun getElementsByName(elementName: String): NodeList
    open fun getElementsByTagName(qualifiedName: String): HTMLCollection
    open fun getElementsByTagNameNS(namespace: String, localName: String): HTMLCollection
    open fun hasFocus(): Boolean
    open fun importNode(node: Node, deep: Boolean): Node
    open fun open(type: String, replace: String): Document
    open fun open(url: String, name: String, features: String): Window
    open fun prepend(nodes: Array)
    open fun queryCommandEnabled(commandId: String): Boolean
    open fun queryCommandIndeterm(commandId: String): Boolean
    open fun queryCommandState(commandId: String): Boolean
    open fun queryCommandSupported(commandId: String): Boolean
    open fun queryCommandValue(commandId: String): String
    open fun querySelector(selectors: String): Element
    open fun querySelectorAll(selectors: String): NodeList
    open fun releaseEvents()
    open fun write(text: Array)
    open fun writeln(text: Array)
}

public external interface DocumentAndElementEventHandlers

public external open class DocumentFragment : Node, NonElementParentNode, ParentNode {
    override val childElementCount: Int
    override val children: HTMLCollection
    override val firstElementChild: Element
    override val lastElementChild: Element
    open fun append(nodes: Array)
    open fun getElementById(elementId: String): Element
    open fun prepend(nodes: Array)
    open fun querySelector(selectors: String): Element
    open fun querySelectorAll(selectors: String): NodeList
}

public external interface DocumentOrShadowRoot

public external interface DocumentReadyState

public external abstract class DocumentType : Node, ChildNode {
    open val name: String
    open val publicId: String
    open val systemId: String
}

public external open class DragEvent : MouseEvent {
    open val dataTransfer: DataTransfer
}

public external interface DragEventInit : MouseEventInit

public external abstract class Element : Node, ParentNode, NonDocumentTypeChildNode, ChildNode, Slotable, GeometryUtils, UnionElementOrHTMLCollection, UnionElementOrRadioNodeList, UnionElementOrMouseEvent, UnionElementOrProcessingInstruction {
    open val attributes: NamedNodeMap
    open val classList: DOMTokenList
    open var className: String
    open val clientHeight: Int
    open val clientLeft: Int
    open val clientTop: Int
    open val clientWidth: Int
    open var id: String
    open var innerHTML: String
    open val localName: String
    open val namespaceURI: String
    open var outerHTML: String
    open val prefix: String
    open val scrollHeight: Int
    open var scrollLeft: Double
    open var scrollTop: Double
    open val scrollWidth: Int
    open val shadowRoot: ShadowRoot
    open var slot: String
    open val tagName: String
    open fun attachShadow(init: ShadowRootInit): ShadowRoot
    open fun closest(selectors: String): Element
    open fun getAttribute(qualifiedName: String): String
    open fun getAttributeNS(namespace: String, localName: String): String
    open fun getAttributeNames(): Array
    open fun getAttributeNode(qualifiedName: String): Attr
    open fun getAttributeNodeNS(namespace: String, localName: String): Attr
    open fun getBoundingClientRect(): DOMRect
    open fun getClientRects(): Array
    open fun getElementsByClassName(classNames: String): HTMLCollection
    open fun getElementsByTagName(qualifiedName: String): HTMLCollection
    open fun getElementsByTagNameNS(namespace: String, localName: String): HTMLCollection
    open fun hasAttribute(qualifiedName: String): Boolean
    open fun hasAttributeNS(namespace: String, localName: String): Boolean
    open fun hasAttributes(): Boolean
    open fun hasPointerCapture(pointerId: Int): Boolean
    open fun insertAdjacentElement(where: String, element: Element): Element
    open fun insertAdjacentHTML(position: String, text: String)
    open fun insertAdjacentText(where: String, data: String)
    open fun matches(selectors: String): Boolean
    open fun releasePointerCapture(pointerId: Int)
    open fun removeAttribute(qualifiedName: String)
    open fun removeAttributeNS(namespace: String, localName: String)
    open fun removeAttributeNode(attr: Attr): Attr
    open fun requestFullscreen(): Promise
    open fun scroll(x: Double, y: Double)
    open fun scroll(options: ScrollToOptions)
    open fun scrollBy(x: Double, y: Double)
    open fun scrollBy(options: ScrollToOptions)
    open fun scrollIntoView()
    open fun scrollIntoView(arg: dynamic)
    open fun scrollTo(x: Double, y: Double)
    open fun scrollTo(options: ScrollToOptions)
    open fun setAttribute(qualifiedName: String, value: String)
    open fun setAttributeNS(namespace: String, qualifiedName: String, value: String)
    open fun setAttributeNode(attr: Attr): Attr
    open fun setAttributeNodeNS(attr: Attr): Attr
    open fun setPointerCapture(pointerId: Int)
    open fun webkitMatchesSelector(selectors: String): Boolean
}

public external interface ElementContentEditable

public external interface ElementCreationOptions

public external interface ElementDefinitionOptions

public external open class ErrorEvent : Event {
    open val colno: Int
    open val error: Any
    open val filename: String
    open val lineno: Int
    open val message: String
}

public external interface ErrorEventInit : EventInit

public external interface EventInit

public external interface EventListenerOptions

public external open class EventSource : EventTarget {
    open var onerror: ((Event) -> dynamic)?
    open var onmessage: ((MessageEvent) -> dynamic)?
    open var onopen: ((Event) -> dynamic)?
    open val readyState: Short
    open val url: String
    open val withCredentials: Boolean
    open fun close()
}

public external interface EventSourceInit

public external interface External

public external interface GeometryUtils

public external interface GetRootNodeOptions

public external interface GlobalEventHandlers

public external abstract class HTMLAllCollection {
    open val length: Int
    open fun item(nameOrIndex: String): UnionElementOrHTMLCollection
    open fun namedItem(name: String): UnionElementOrHTMLCollection
}

public external abstract class HTMLAnchorElement : HTMLElement, HTMLHyperlinkElementUtils {
    open var charset: String
    open var coords: String
    open var download: String
    open var hreflang: String
    open var name: String
    open var ping: String
    open var referrerPolicy: String
    open var rel: String
    open val relList: DOMTokenList
    open var rev: String
    open var shape: String
    open var target: String
    open var text: String
    open var type: String
}

public external abstract class HTMLAppletElement : HTMLElement {
    open var _object: String
    open var align: String
    open var alt: String
    open var archive: String
    open var code: String
    open var codeBase: String
    open var height: String
    open var hspace: Int
    open var name: String
    open var vspace: Int
    open var width: String
}

public external abstract class HTMLAreaElement : HTMLElement, HTMLHyperlinkElementUtils {
    open var alt: String
    open var coords: String
    open var download: String
    open var noHref: Boolean
    open var ping: String
    open var referrerPolicy: String
    open var rel: String
    open val relList: DOMTokenList
    open var shape: String
    open var target: String
}

public external abstract class HTMLAudioElement : HTMLMediaElement

public external abstract class HTMLBRElement : HTMLElement {
    open var clear: String
}

public external abstract class HTMLBaseElement : HTMLElement {
    open var href: String
    open var target: String
}

public external abstract class HTMLBodyElement : HTMLElement, WindowEventHandlers {
    open var aLink: String
    open var background: String
    open var bgColor: String
    open var link: String
    open var text: String
    open var vLink: String
}

public external abstract class HTMLButtonElement : HTMLElement {
    open var autofocus: Boolean
    open var disabled: Boolean
    open val form: HTMLFormElement
    open var formAction: String
    open var formEnctype: String
    open var formMethod: String
    open var formNoValidate: Boolean
    open var formTarget: String
    open val labels: NodeList
    open var menu: HTMLMenuElement
    open var name: String
    open var type: String
    open val validationMessage: String
    open val validity: ValidityState
    open var value: String
    open val willValidate: Boolean
    open fun checkValidity(): Boolean
    open fun reportValidity(): Boolean
    open fun setCustomValidity(error: String)
}

public external abstract class HTMLCanvasElement : HTMLElement, CanvasImageSource, TexImageSource {
    open var height: Int
    open var width: Int
    open fun getContext(contextId: String, arguments: Array): RenderingContext
    open fun toBlob(_callback: ((Blob) -> Unit)?, type: String, quality: Any)
    open fun toDataURL(type: String, quality: Any): String
}

public external abstract class HTMLCollection : ItemArrayLike<Element>, UnionElementOrHTMLCollection {
    open fun item(index: Int): Element
    open fun namedItem(name: String): Element
}

public external abstract class HTMLDListElement : HTMLElement {
    open var compact: Boolean
}

public external abstract class HTMLDataElement : HTMLElement {
    open var value: String
}

public external abstract class HTMLDataListElement : HTMLElement {
    open val options: HTMLCollection
}

public external abstract class HTMLDetailsElement : HTMLElement {
    open var open: Boolean
}

public external abstract class HTMLDialogElement : HTMLElement {
    open var open: Boolean
    open var returnValue: String
    open fun close(returnValue: String)
    open fun show(anchor: UnionElementOrMouseEvent)
    open fun showModal(anchor: UnionElementOrMouseEvent)
}

public external abstract class HTMLDirectoryElement : HTMLElement {
    open var compact: Boolean
}

public external abstract class HTMLDivElement : HTMLElement {
    open var align: String
}

public external abstract class HTMLElement : Element, GlobalEventHandlers, DocumentAndElementEventHandlers, ElementContentEditable, ElementCSSInlineStyle {
    open var accessKey: String
    open val accessKeyLabel: String
    open var contextMenu: HTMLMenuElement
    open val dataset: DOMStringMap
    open var dir: String
    open var draggable: Boolean
    open val dropzone: DOMTokenList
    open var hidden: Boolean
    open var innerText: String
    open var lang: String
    open val offsetHeight: Int
    open val offsetLeft: Int
    open val offsetParent: Element
    open val offsetTop: Int
    open val offsetWidth: Int
    open var spellcheck: Boolean
    open var tabIndex: Int
    open var title: String
    open var translate: Boolean
    open fun blur()
    open fun click()
    open fun focus()
    open fun forceSpellCheck()
}

public external abstract class HTMLEmbedElement : HTMLElement {
    open var align: String
    open var height: String
    open var name: String
    open var src: String
    open var type: String
    open var width: String
    open fun getSVGDocument(): Document
}

public external abstract class HTMLFieldSetElement : HTMLElement {
    open var disabled: Boolean
    open val elements: HTMLCollection
    open val form: HTMLFormElement
    open var name: String
    open val type: String
    open val validationMessage: String
    open val validity: ValidityState
    open val willValidate: Boolean
    open fun checkValidity(): Boolean
    open fun reportValidity(): Boolean
    open fun setCustomValidity(error: String)
}

public external abstract class HTMLFontElement : HTMLElement {
    open var color: String
    open var face: String
    open var size: String
}

public external abstract class HTMLFormControlsCollection : HTMLCollection

public external abstract class HTMLFormElement : HTMLElement {
    open var acceptCharset: String
    open var action: String
    open var autocomplete: String
    open val elements: HTMLFormControlsCollection
    open var encoding: String
    open var enctype: String
    open val length: Int
    open var method: String
    open var name: String
    open var noValidate: Boolean
    open var target: String
    open fun checkValidity(): Boolean
    open fun reportValidity(): Boolean
    open fun reset()
    open fun submit()
}

public external abstract class HTMLFrameElement : HTMLElement {
    open val contentDocument: Document
    open val contentWindow: Window
    open var frameBorder: String
    open var longDesc: String
    open var marginHeight: String
    open var marginWidth: String
    open var name: String
    open var noResize: Boolean
    open var scrolling: String
    open var src: String
}

public external abstract class HTMLFrameSetElement : HTMLElement, WindowEventHandlers {
    open var cols: String
    open var rows: String
}

public external abstract class HTMLHRElement : HTMLElement {
    open var align: String
    open var color: String
    open var noShade: Boolean
    open var size: String
    open var width: String
}

public external abstract class HTMLHeadElement : HTMLElement

public external abstract class HTMLHeadingElement : HTMLElement {
    open var align: String
}

public external abstract class HTMLHtmlElement : HTMLElement {
    open var version: String
}

public external interface HTMLHyperlinkElementUtils

public external abstract class HTMLIFrameElement : HTMLElement {
    open var align: String
    open var allowFullscreen: Boolean
    open var allowUserMedia: Boolean
    open val contentDocument: Document
    open val contentWindow: Window
    open var frameBorder: String
    open var height: String
    open var longDesc: String
    open var marginHeight: String
    open var marginWidth: String
    open var name: String
    open var referrerPolicy: String
    open val sandbox: DOMTokenList
    open var scrolling: String
    open var src: String
    open var srcdoc: String
    open var width: String
    open fun getSVGDocument(): Document
}

public external abstract class HTMLImageElement : HTMLElement, HTMLOrSVGImageElement, TexImageSource {
    open var align: String
    open var alt: String
    open var border: String
    open val complete: Boolean
    open var crossOrigin: String
    open val currentSrc: String
    open var height: Int
    open var hspace: Int
    open var isMap: Boolean
    open var longDesc: String
    open var lowsrc: String
    open var name: String
    open val naturalHeight: Int
    open val naturalWidth: Int
    open var referrerPolicy: String
    open var sizes: String
    open var src: String
    open var srcset: String
    open var useMap: String
    open var vspace: Int
    open var width: Int
    open val x: Int
    open val y: Int
}

public external abstract class HTMLInputElement : HTMLElement {
    open var accept: String
    open var align: String
    open var alt: String
    open var autocomplete: String
    open var autofocus: Boolean
    open var checked: Boolean
    open var defaultChecked: Boolean
    open var defaultValue: String
    open var dirName: String
    open var disabled: Boolean
    open val files: FileList
    open val form: HTMLFormElement
    open var formAction: String
    open var formEnctype: String
    open var formMethod: String
    open var formNoValidate: Boolean
    open var formTarget: String
    open var height: Int
    open var indeterminate: Boolean
    open var inputMode: String
    open val labels: NodeList
    open val list: HTMLElement
    open var max: String
    open var maxLength: Int
    open var min: String
    open var minLength: Int
    open var multiple: Boolean
    open var name: String
    open var pattern: String
    open var placeholder: String
    open var readOnly: Boolean
    open var required: Boolean
    open var selectionDirection: String
    open var selectionEnd: Int
    open var selectionStart: Int
    open var size: Int
    open var src: String
    open var step: String
    open var type: String
    open var useMap: String
    open val validationMessage: String
    open val validity: ValidityState
    open var value: String
    open var valueAsDate: dynamic
    open var valueAsNumber: Double
    open var width: Int
    open val willValidate: Boolean
    open fun checkValidity(): Boolean
    open fun reportValidity(): Boolean
    open fun select()
    open fun setCustomValidity(error: String)
    open fun setRangeText(replacement: String)
    open fun setRangeText(replacement: String, start: Int, end: Int, selectionMode: SelectionMode)
    open fun setSelectionRange(start: Int, end: Int, direction: String)
    open fun stepDown(n: Int)
    open fun stepUp(n: Int)
}

public external abstract class HTMLKeygenElement : HTMLElement {
    open var autofocus: Boolean
    open var challenge: String
    open var disabled: Boolean
    open val form: HTMLFormElement
    open var keytype: String
    open val labels: NodeList
    open var name: String
    open val type: String
    open val validationMessage: String
    open val validity: ValidityState
    open val willValidate: Boolean
    open fun checkValidity(): Boolean
    open fun reportValidity(): Boolean
    open fun setCustomValidity(error: String)
}

public external abstract class HTMLLIElement : HTMLElement {
    open var type: String
    open var value: Int
}

public external abstract class HTMLLabelElement : HTMLElement {
    open val control: HTMLElement
    open val form: HTMLFormElement
    open var htmlFor: String
}

public external abstract class HTMLLegendElement : HTMLElement {
    open var align: String
    open val form: HTMLFormElement
}

public external abstract class HTMLLinkElement : HTMLElement, LinkStyle {
    open var as: RequestDestination
    open var charset: String
    open var crossOrigin: String
    open var href: String
    open var hreflang: String
    open var media: String
    open var nonce: String
    open var referrerPolicy: String
    open var rel: String
    open val relList: DOMTokenList
    open var rev: String
    open var scope: String
    open val sizes: DOMTokenList
    open var target: String
    open var type: String
    open var workerType: WorkerType
}

public external abstract class HTMLMapElement : HTMLElement {
    open val areas: HTMLCollection
    open var name: String
}

public external abstract class HTMLMarqueeElement : HTMLElement {
    open var behavior: String
    open var bgColor: String
    open var direction: String
    open var height: String
    open var hspace: Int
    open var loop: Int
    open var onbounce: ((Event) -> dynamic)?
    open var onfinish: ((Event) -> dynamic)?
    open var onstart: ((Event) -> dynamic)?
    open var scrollAmount: Int
    open var scrollDelay: Int
    open var trueSpeed: Boolean
    open var vspace: Int
    open var width: String
    open fun start()
    open fun stop()
}

public external abstract class HTMLMediaElement : HTMLElement {
    open val audioTracks: AudioTrackList
    open var autoplay: Boolean
    open val buffered: TimeRanges
    open var controls: Boolean
    open var crossOrigin: String
    open val currentSrc: String
    open var currentTime: Double
    open var defaultMuted: Boolean
    open var defaultPlaybackRate: Double
    open val duration: Double
    open val ended: Boolean
    open val error: MediaError
    open var loop: Boolean
    open val mediaKeys: MediaKeys
    open var muted: Boolean
    open val networkState: Short
    open var onencrypted: ((Event) -> dynamic)?
    open var onwaitingforkey: ((Event) -> dynamic)?
    open val paused: Boolean
    open var playbackRate: Double
    open val played: TimeRanges
    open var preload: String
    open val readyState: Short
    open val seekable: TimeRanges
    open val seeking: Boolean
    open var src: String
    open var srcObject: MediaProvider
    open val textTracks: TextTrackList
    open val videoTracks: VideoTrackList
    open var volume: Double
    open fun addTextTrack(kind: TextTrackKind, label: String, language: String): TextTrack
    open fun canPlayType(type: String): CanPlayTypeResult
    open fun fastSeek(time: Double)
    open fun getStartDate(): dynamic
    open fun load()
    open fun pause()
    open fun play(): Promise
    open fun setMediaKeys(mediaKeys: MediaKeys): Promise
}

public external abstract class HTMLMenuElement : HTMLElement {
    open var compact: Boolean
    open var label: String
    open var type: String
}

public external abstract class HTMLMenuItemElement : HTMLElement {
    open var checked: Boolean
    open var default: Boolean
    open var disabled: Boolean
    open var icon: String
    open var label: String
    open var radiogroup: String
    open var type: String
}

public external abstract class HTMLMetaElement : HTMLElement {
    open var content: String
    open var httpEquiv: String
    open var name: String
    open var scheme: String
}

public external abstract class HTMLMeterElement : HTMLElement {
    open var high: Double
    open val labels: NodeList
    open var low: Double
    open var max: Double
    open var min: Double
    open var optimum: Double
    open var value: Double
}

public external abstract class HTMLModElement : HTMLElement {
    open var cite: String
    open var dateTime: String
}

public external abstract class HTMLOListElement : HTMLElement {
    open var compact: Boolean
    open var reversed: Boolean
    open var start: Int
    open var type: String
}

public external abstract class HTMLObjectElement : HTMLElement {
    open var align: String
    open var archive: String
    open var border: String
    open var code: String
    open var codeBase: String
    open var codeType: String
    open val contentDocument: Document
    open val contentWindow: Window
    open var data: String
    open var declare: Boolean
    open val form: HTMLFormElement
    open var height: String
    open var hspace: Int
    open var name: String
    open var standby: String
    open var type: String
    open var typeMustMatch: Boolean
    open var useMap: String
    open val validationMessage: String
    open val validity: ValidityState
    open var vspace: Int
    open var width: String
    open val willValidate: Boolean
    open fun checkValidity(): Boolean
    open fun getSVGDocument(): Document
    open fun reportValidity(): Boolean
    open fun setCustomValidity(error: String)
}

public external abstract class HTMLOptGroupElement : HTMLElement, UnionHTMLOptGroupElementOrHTMLOptionElement {
    open var disabled: Boolean
    open var label: String
}

public external abstract class HTMLOptionElement : HTMLElement, UnionHTMLOptGroupElementOrHTMLOptionElement {
    open var defaultSelected: Boolean
    open var disabled: Boolean
    open val form: HTMLFormElement
    open val index: Int
    open var label: String
    open var selected: Boolean
    open var text: String
    open var value: String
}

public external abstract class HTMLOptionsCollection : HTMLCollection {
    override var length: Int
    open var selectedIndex: Int
    open fun add(element: UnionHTMLOptGroupElementOrHTMLOptionElement, before: dynamic)
    open fun remove(index: Int)
}

public external interface HTMLOrSVGImageElement : CanvasImageSource

public external interface HTMLOrSVGScriptElement

public external abstract class HTMLOutputElement : HTMLElement {
    open var defaultValue: String
    open val form: HTMLFormElement
    open val htmlFor: DOMTokenList
    open val labels: NodeList
    open var name: String
    open val type: String
    open val validationMessage: String
    open val validity: ValidityState
    open var value: String
    open val willValidate: Boolean
    open fun checkValidity(): Boolean
    open fun reportValidity(): Boolean
    open fun setCustomValidity(error: String)
}

public external abstract class HTMLParagraphElement : HTMLElement {
    open var align: String
}

public external abstract class HTMLParamElement : HTMLElement {
    open var name: String
    open var type: String
    open var value: String
    open var valueType: String
}

public external abstract class HTMLPictureElement : HTMLElement

public external abstract class HTMLPreElement : HTMLElement {
    open var width: Int
}

public external abstract class HTMLProgressElement : HTMLElement {
    open val labels: NodeList
    open var max: Double
    open val position: Double
    open var value: Double
}

public external abstract class HTMLQuoteElement : HTMLElement {
    open var cite: String
}

public external abstract class HTMLScriptElement : HTMLElement, HTMLOrSVGScriptElement {
    open var async: Boolean
    open var charset: String
    open var crossOrigin: String
    open var defer: Boolean
    open var event: String
    open var htmlFor: String
    open var nonce: String
    open var src: String
    open var text: String
    open var type: String
}

public external abstract class HTMLSelectElement : HTMLElement, ItemArrayLike<Element> {
    open var autocomplete: String
    open var autofocus: Boolean
    open var disabled: Boolean
    open val form: HTMLFormElement
    open val labels: NodeList
    override var length: Int
    open var multiple: Boolean
    open var name: String
    open val options: HTMLOptionsCollection
    open var required: Boolean
    open var selectedIndex: Int
    open val selectedOptions: HTMLCollection
    open var size: Int
    open val type: String
    open val validationMessage: String
    open val validity: ValidityState
    open var value: String
    open val willValidate: Boolean
    open fun add(element: UnionHTMLOptGroupElementOrHTMLOptionElement, before: dynamic)
    open fun checkValidity(): Boolean
    open fun item(index: Int): Element
    open fun namedItem(name: String): HTMLOptionElement
    open fun remove(index: Int)
    open fun reportValidity(): Boolean
    open fun setCustomValidity(error: String)
}

public external abstract class HTMLSlotElement : HTMLElement {
    open var name: String
    open fun assignedNodes(options: AssignedNodesOptions): Array
}

public external abstract class HTMLSourceElement : HTMLElement {
    open var media: String
    open var sizes: String
    open var src: String
    open var srcset: String
    open var type: String
}

public external abstract class HTMLSpanElement : HTMLElement

public external abstract class HTMLStyleElement : HTMLElement, LinkStyle {
    open var media: String
    open var nonce: String
    open var type: String
}

public external abstract class HTMLTableCaptionElement : HTMLElement {
    open var align: String
}

public external abstract class HTMLTableCellElement : HTMLElement {
    open var abbr: String
    open var align: String
    open var axis: String
    open var bgColor: String
    open val cellIndex: Int
    open var ch: String
    open var chOff: String
    open var colSpan: Int
    open var headers: String
    open var height: String
    open var noWrap: Boolean
    open var rowSpan: Int
    open var scope: String
    open var vAlign: String
    open var width: String
}

public external abstract class HTMLTableColElement : HTMLElement {
    open var align: String
    open var ch: String
    open var chOff: String
    open var span: Int
    open var vAlign: String
    open var width: String
}

public external abstract class HTMLTableElement : HTMLElement {
    open var align: String
    open var bgColor: String
    open var border: String
    open var caption: HTMLTableCaptionElement
    open var cellPadding: String
    open var cellSpacing: String
    open var frame: String
    open val rows: HTMLCollection
    open var rules: String
    open var summary: String
    open val tBodies: HTMLCollection
    open var tFoot: HTMLTableSectionElement
    open var tHead: HTMLTableSectionElement
    open var width: String
    open fun createCaption(): HTMLTableCaptionElement
    open fun createTBody(): HTMLTableSectionElement
    open fun createTFoot(): HTMLTableSectionElement
    open fun createTHead(): HTMLTableSectionElement
    open fun deleteCaption()
    open fun deleteRow(index: Int)
    open fun deleteTFoot()
    open fun deleteTHead()
    open fun insertRow(index: Int): HTMLTableRowElement
}

public external abstract class HTMLTableRowElement : HTMLElement {
    open var align: String
    open var bgColor: String
    open val cells: HTMLCollection
    open var ch: String
    open var chOff: String
    open val rowIndex: Int
    open val sectionRowIndex: Int
    open var vAlign: String
    open fun deleteCell(index: Int)
    open fun insertCell(index: Int): HTMLElement
}

public external abstract class HTMLTableSectionElement : HTMLElement {
    open var align: String
    open var ch: String
    open var chOff: String
    open val rows: HTMLCollection
    open var vAlign: String
    open fun deleteRow(index: Int)
    open fun insertRow(index: Int): HTMLElement
}

public external abstract class HTMLTemplateElement : HTMLElement {
    open val content: DocumentFragment
}

public external abstract class HTMLTextAreaElement : HTMLElement {
    open var autocomplete: String
    open var autofocus: Boolean
    open var cols: Int
    open var defaultValue: String
    open var dirName: String
    open var disabled: Boolean
    open val form: HTMLFormElement
    open var inputMode: String
    open val labels: NodeList
    open var maxLength: Int
    open var minLength: Int
    open var name: String
    open var placeholder: String
    open var readOnly: Boolean
    open var required: Boolean
    open var rows: Int
    open var selectionDirection: String
    open var selectionEnd: Int
    open var selectionStart: Int
    open val textLength: Int
    open val type: String
    open val validationMessage: String
    open val validity: ValidityState
    open var value: String
    open val willValidate: Boolean
    open var wrap: String
    open fun checkValidity(): Boolean
    open fun reportValidity(): Boolean
    open fun select()
    open fun setCustomValidity(error: String)
    open fun setRangeText(replacement: String)
    open fun setRangeText(replacement: String, start: Int, end: Int, selectionMode: SelectionMode)
    open fun setSelectionRange(start: Int, end: Int, direction: String)
}

public external abstract class HTMLTimeElement : HTMLElement {
    open var dateTime: String
}

public external abstract class HTMLTitleElement : HTMLElement {
    open var text: String
}

public external abstract class HTMLTrackElement : HTMLElement {
    open var default: Boolean
    open var kind: String
    open var label: String
    open val readyState: Short
    open var src: String
    open var srclang: String
    open val track: TextTrack
}

public external abstract class HTMLUListElement : HTMLElement {
    open var compact: Boolean
    open var type: String
}

public external abstract class HTMLUnknownElement : HTMLElement

public external abstract class HTMLVideoElement : HTMLMediaElement, CanvasImageSource, TexImageSource {
    open var height: Int
    open var playsInline: Boolean
    open var poster: String
    open val videoHeight: Int
    open val videoWidth: Int
    open var width: Int
}

public external open class HashChangeEvent : Event {
    open val newURL: String
    open val oldURL: String
}

public external interface HashChangeEventInit : EventInit

public external abstract class History {
    open val length: Int
    open var scrollRestoration: ScrollRestoration
    open val state: Any
    open fun back()
    open fun forward()
    open fun go(delta: Int)
    open fun pushState(data: Any, title: String, url: String)
    open fun replaceState(data: Any, title: String, url: String)
}

public external interface HitRegionOptions

public external open class Image : HTMLImageElement {
    override val assignedSlot: HTMLSlotElement
    override val childElementCount: Int
    override val children: HTMLCollection
    override var contentEditable: String
    override val firstElementChild: Element
    override val isContentEditable: Boolean
    override val lastElementChild: Element
    override val nextElementSibling: Element
    override var onabort: ((Event) -> dynamic)?
    override var onblur: ((FocusEvent) -> dynamic)?
    override var oncancel: ((Event) -> dynamic)?
    override var oncanplay: ((Event) -> dynamic)?
    override var oncanplaythrough: ((Event) -> dynamic)?
    override var onchange: ((Event) -> dynamic)?
    override var onclick: ((MouseEvent) -> dynamic)?
    override var onclose: ((Event) -> dynamic)?
    override var oncontextmenu: ((MouseEvent) -> dynamic)?
    override var oncopy: ((ClipboardEvent) -> dynamic)?
    override var oncuechange: ((Event) -> dynamic)?
    override var oncut: ((ClipboardEvent) -> dynamic)?
    override var ondblclick: ((MouseEvent) -> dynamic)?
    override var ondrag: ((DragEvent) -> dynamic)?
    override var ondragend: ((DragEvent) -> dynamic)?
    override var ondragenter: ((DragEvent) -> dynamic)?
    override var ondragexit: ((DragEvent) -> dynamic)?
    override var ondragleave: ((DragEvent) -> dynamic)?
    override var ondragover: ((DragEvent) -> dynamic)?
    override var ondragstart: ((DragEvent) -> dynamic)?
    override var ondrop: ((DragEvent) -> dynamic)?
    override var ondurationchange: ((Event) -> dynamic)?
    override var onemptied: ((Event) -> dynamic)?
    override var onended: ((Event) -> dynamic)?
    override var onerror: Function5
    override var onfocus: ((FocusEvent) -> dynamic)?
    override var ongotpointercapture: ((PointerEvent) -> dynamic)?
    override var oninput: ((InputEvent) -> dynamic)?
    override var oninvalid: ((Event) -> dynamic)?
    override var onkeydown: ((KeyboardEvent) -> dynamic)?
    override var onkeypress: ((KeyboardEvent) -> dynamic)?
    override var onkeyup: ((KeyboardEvent) -> dynamic)?
    override var onload: ((Event) -> dynamic)?
    override var onloadeddata: ((Event) -> dynamic)?
    override var onloadedmetadata: ((Event) -> dynamic)?
    override var onloadend: ((Event) -> dynamic)?
    override var onloadstart: ((ProgressEvent) -> dynamic)?
    override var onlostpointercapture: ((PointerEvent) -> dynamic)?
    override var onmousedown: ((MouseEvent) -> dynamic)?
    override var onmouseenter: ((MouseEvent) -> dynamic)?
    override var onmouseleave: ((MouseEvent) -> dynamic)?
    override var onmousemove: ((MouseEvent) -> dynamic)?
    override var onmouseout: ((MouseEvent) -> dynamic)?
    override var onmouseover: ((MouseEvent) -> dynamic)?
    override var onmouseup: ((MouseEvent) -> dynamic)?
    override var onpaste: ((ClipboardEvent) -> dynamic)?
    override var onpause: ((Event) -> dynamic)?
    override var onplay: ((Event) -> dynamic)?
    override var onplaying: ((Event) -> dynamic)?
    override var onpointercancel: ((PointerEvent) -> dynamic)?
    override var onpointerdown: ((PointerEvent) -> dynamic)?
    override var onpointerenter: ((PointerEvent) -> dynamic)?
    override var onpointerleave: ((PointerEvent) -> dynamic)?
    override var onpointermove: ((PointerEvent) -> dynamic)?
    override var onpointerout: ((PointerEvent) -> dynamic)?
    override var onpointerover: ((PointerEvent) -> dynamic)?
    override var onpointerup: ((PointerEvent) -> dynamic)?
    override var onprogress: ((ProgressEvent) -> dynamic)?
    override var onratechange: ((Event) -> dynamic)?
    override var onreset: ((Event) -> dynamic)?
    override var onresize: ((Event) -> dynamic)?
    override var onscroll: ((Event) -> dynamic)?
    override var onseeked: ((Event) -> dynamic)?
    override var onseeking: ((Event) -> dynamic)?
    override var onselect: ((Event) -> dynamic)?
    override var onshow: ((Event) -> dynamic)?
    override var onstalled: ((Event) -> dynamic)?
    override var onsubmit: ((Event) -> dynamic)?
    override var onsuspend: ((Event) -> dynamic)?
    override var ontimeupdate: ((Event) -> dynamic)?
    override var ontoggle: ((Event) -> dynamic)?
    override var onvolumechange: ((Event) -> dynamic)?
    override var onwaiting: ((Event) -> dynamic)?
    override var onwheel: ((WheelEvent) -> dynamic)?
    override val previousElementSibling: Element
    override val style: CSSStyleDeclaration
    open fun after(nodes: Array)
    open fun append(nodes: Array)
    open fun before(nodes: Array)
    open fun convertPointFromNode(point: DOMPointInit, from: dynamic, options: ConvertCoordinateOptions): DOMPoint
    open fun convertQuadFromNode(quad: dynamic, from: dynamic, options: ConvertCoordinateOptions): DOMQuad
    open fun convertRectFromNode(rect: DOMRectReadOnly, from: dynamic, options: ConvertCoordinateOptions): DOMQuad
    open fun getBoxQuads(options: BoxQuadOptions): Array
    open fun prepend(nodes: Array)
    open fun querySelector(selectors: String): Element
    open fun querySelectorAll(selectors: String): NodeList
    open fun remove()
    open fun replaceWith(nodes: Array)
}

public external abstract class ImageBitmap : CanvasImageSource, TexImageSource {
    open val height: Int
    open val width: Int
    open fun close()
}

public external interface ImageBitmapOptions

public external abstract class ImageBitmapRenderingContext {
    open val canvas: HTMLCanvasElement
    open fun transferFromImageBitmap(bitmap: ImageBitmap)
}

public external interface ImageBitmapRenderingContextSettings

public external interface ImageBitmapSource

public external open class ImageData : ImageBitmapSource, TexImageSource {
    open val data: Uint8ClampedArray
    open val height: Int
    open val width: Int
}

public external interface ImageOrientation

public external interface ImageSmoothingQuality

public external interface ItemArrayLike<T>

public external abstract class Location {
    open val ancestorOrigins: Array
    open var hash: String
    open var host: String
    open var hostname: String
    open var href: String
    open val origin: String
    open var pathname: String
    open var port: String
    open var protocol: String
    open var search: String
    open fun assign(url: String)
    open fun reload()
    open fun replace(url: String)
}

public external abstract class MediaError {
    open val code: Short
}

public external interface MediaProvider

public external abstract class MediaQueryList : EventTarget {
    open val matches: Boolean
    open val media: String
    open var onchange: ((Event) -> dynamic)?
    open fun addListener(listener: ((Event) -> Unit)?)
    open fun addListener(listener: EventListener)
    open fun removeListener(listener: ((Event) -> Unit)?)
    open fun removeListener(listener: EventListener)
}

public external open class MediaQueryListEvent : Event {
    open val matches: Boolean
    open val media: String
}

public external interface MediaQueryListEventInit : EventInit

public external open class MessageChannel {
    open val port1: MessagePort
    open val port2: MessagePort
}

public external open class MessageEvent : Event {
    open val data: Any
    open val lastEventId: String
    open val origin: String
    open val ports: Array
    open val source: UnionMessagePortOrWindowProxy
    open fun initMessageEvent(type: String, bubbles: Boolean, cancelable: Boolean, data: Any, origin: String, lastEventId: String, source: UnionMessagePortOrWindowProxy, ports: Array)
}

public external interface MessageEventInit : EventInit

public external abstract class MessagePort : EventTarget, UnionMessagePortOrWindowProxy, UnionMessagePortOrServiceWorker, UnionClientOrMessagePortOrServiceWorker {
    open var onmessage: ((MessageEvent) -> dynamic)?
    open fun close()
    open fun postMessage(message: Any, transfer: Array)
    open fun start()
}

public external abstract class MimeType {
    open val description: String
    open val enabledPlugin: Plugin
    open val suffixes: String
    open val type: String
}

public external abstract class MimeTypeArray : ItemArrayLike<MimeType> {
    open fun item(index: Int): MimeType
    open fun namedItem(name: String): MimeType
}

public external open class MutationObserver {
    open fun disconnect()
    open fun observe(target: Node, options: MutationObserverInit)
    open fun takeRecords(): Array
}

public external interface MutationObserverInit

public external abstract class MutationRecord {
    open val addedNodes: NodeList
    open val attributeName: String
    open val attributeNamespace: String
    open val nextSibling: Node
    open val oldValue: String
    open val previousSibling: Node
    open val removedNodes: NodeList
    open val target: Node
    open val type: String
}

public external abstract class NamedNodeMap : ItemArrayLike<Attr> {
    open fun getNamedItem(qualifiedName: String): Attr
    open fun getNamedItemNS(namespace: String, localName: String): Attr
    open fun item(index: Int): Attr
    open fun removeNamedItem(qualifiedName: String): Attr
    open fun removeNamedItemNS(namespace: String, localName: String): Attr
    open fun setNamedItem(attr: Attr): Attr
    open fun setNamedItemNS(attr: Attr): Attr
}

public external abstract class Navigator : NavigatorID, NavigatorLanguage, NavigatorOnLine, NavigatorContentUtils, NavigatorCookies, NavigatorPlugins, NavigatorConcurrentHardware {
    open val clipboard: Clipboard
    open val maxTouchPoints: Int
    open val mediaDevices: MediaDevices
    open val serviceWorker: ServiceWorkerContainer
    open fun getUserMedia(constraints: MediaStreamConstraints, successCallback: ((MediaStream) -> Unit)?, errorCallback: ((dynamic) -> Unit)?)
    open fun requestMediaKeySystemAccess(keySystem: String, supportedConfigurations: Array): Promise
    open fun vibrate(pattern: dynamic): Boolean
}

public external interface NavigatorConcurrentHardware

public external interface NavigatorContentUtils

public external interface NavigatorCookies

public external interface NavigatorID

public external interface NavigatorLanguage

public external interface NavigatorOnLine

public external interface NavigatorPlugins

public external abstract class Node : EventTarget {
    open val baseURI: String
    open val childNodes: NodeList
    open val firstChild: Node
    open val isConnected: Boolean
    open val lastChild: Node
    open val nextSibling: Node
    open val nodeName: String
    open val nodeType: Short
    open var nodeValue: String
    open val ownerDocument: Document
    open val parentElement: Element
    open val parentNode: Node
    open val previousSibling: Node
    open var textContent: String
    open fun appendChild(node: Node): Node
    open fun cloneNode(deep: Boolean): Node
    open fun compareDocumentPosition(other: Node): Short
    open fun contains(other: Node): Boolean
    open fun getRootNode(options: GetRootNodeOptions): Node
    open fun hasChildNodes(): Boolean
    open fun insertBefore(node: Node, child: Node): Node
    open fun isDefaultNamespace(namespace: String): Boolean
    open fun isEqualNode(otherNode: Node): Boolean
    open fun isSameNode(otherNode: Node): Boolean
    open fun lookupNamespaceURI(prefix: String): String
    open fun lookupPrefix(namespace: String): String
    open fun normalize()
    open fun removeChild(child: Node): Node
    open fun replaceChild(node: Node, child: Node): Node
}

public external interface NodeFilter

public external abstract class NodeIterator {
    open val filter: NodeFilter
    open val pointerBeforeReferenceNode: Boolean
    open val referenceNode: Node
    open val root: Node
    open val whatToShow: Int
    open fun detach()
    open fun nextNode(): Node
    open fun previousNode(): Node
}

public external abstract class NodeList : ItemArrayLike<Node> {
    open fun item(index: Int): Node
}

public external interface NonDocumentTypeChildNode

public external interface NonElementParentNode

public external open class Option : HTMLOptionElement {
    override val assignedSlot: HTMLSlotElement
    override val childElementCount: Int
    override val children: HTMLCollection
    override var contentEditable: String
    override val firstElementChild: Element
    override val isContentEditable: Boolean
    override val lastElementChild: Element
    override val nextElementSibling: Element
    override var onabort: ((Event) -> dynamic)?
    override var onblur: ((FocusEvent) -> dynamic)?
    override var oncancel: ((Event) -> dynamic)?
    override var oncanplay: ((Event) -> dynamic)?
    override var oncanplaythrough: ((Event) -> dynamic)?
    override var onchange: ((Event) -> dynamic)?
    override var onclick: ((MouseEvent) -> dynamic)?
    override var onclose: ((Event) -> dynamic)?
    override var oncontextmenu: ((MouseEvent) -> dynamic)?
    override var oncopy: ((ClipboardEvent) -> dynamic)?
    override var oncuechange: ((Event) -> dynamic)?
    override var oncut: ((ClipboardEvent) -> dynamic)?
    override var ondblclick: ((MouseEvent) -> dynamic)?
    override var ondrag: ((DragEvent) -> dynamic)?
    override var ondragend: ((DragEvent) -> dynamic)?
    override var ondragenter: ((DragEvent) -> dynamic)?
    override var ondragexit: ((DragEvent) -> dynamic)?
    override var ondragleave: ((DragEvent) -> dynamic)?
    override var ondragover: ((DragEvent) -> dynamic)?
    override var ondragstart: ((DragEvent) -> dynamic)?
    override var ondrop: ((DragEvent) -> dynamic)?
    override var ondurationchange: ((Event) -> dynamic)?
    override var onemptied: ((Event) -> dynamic)?
    override var onended: ((Event) -> dynamic)?
    override var onerror: Function5
    override var onfocus: ((FocusEvent) -> dynamic)?
    override var ongotpointercapture: ((PointerEvent) -> dynamic)?
    override var oninput: ((InputEvent) -> dynamic)?
    override var oninvalid: ((Event) -> dynamic)?
    override var onkeydown: ((KeyboardEvent) -> dynamic)?
    override var onkeypress: ((KeyboardEvent) -> dynamic)?
    override var onkeyup: ((KeyboardEvent) -> dynamic)?
    override var onload: ((Event) -> dynamic)?
    override var onloadeddata: ((Event) -> dynamic)?
    override var onloadedmetadata: ((Event) -> dynamic)?
    override var onloadend: ((Event) -> dynamic)?
    override var onloadstart: ((ProgressEvent) -> dynamic)?
    override var onlostpointercapture: ((PointerEvent) -> dynamic)?
    override var onmousedown: ((MouseEvent) -> dynamic)?
    override var onmouseenter: ((MouseEvent) -> dynamic)?
    override var onmouseleave: ((MouseEvent) -> dynamic)?
    override var onmousemove: ((MouseEvent) -> dynamic)?
    override var onmouseout: ((MouseEvent) -> dynamic)?
    override var onmouseover: ((MouseEvent) -> dynamic)?
    override var onmouseup: ((MouseEvent) -> dynamic)?
    override var onpaste: ((ClipboardEvent) -> dynamic)?
    override var onpause: ((Event) -> dynamic)?
    override var onplay: ((Event) -> dynamic)?
    override var onplaying: ((Event) -> dynamic)?
    override var onpointercancel: ((PointerEvent) -> dynamic)?
    override var onpointerdown: ((PointerEvent) -> dynamic)?
    override var onpointerenter: ((PointerEvent) -> dynamic)?
    override var onpointerleave: ((PointerEvent) -> dynamic)?
    override var onpointermove: ((PointerEvent) -> dynamic)?
    override var onpointerout: ((PointerEvent) -> dynamic)?
    override var onpointerover: ((PointerEvent) -> dynamic)?
    override var onpointerup: ((PointerEvent) -> dynamic)?
    override var onprogress: ((ProgressEvent) -> dynamic)?
    override var onratechange: ((Event) -> dynamic)?
    override var onreset: ((Event) -> dynamic)?
    override var onresize: ((Event) -> dynamic)?
    override var onscroll: ((Event) -> dynamic)?
    override var onseeked: ((Event) -> dynamic)?
    override var onseeking: ((Event) -> dynamic)?
    override var onselect: ((Event) -> dynamic)?
    override var onshow: ((Event) -> dynamic)?
    override var onstalled: ((Event) -> dynamic)?
    override var onsubmit: ((Event) -> dynamic)?
    override var onsuspend: ((Event) -> dynamic)?
    override var ontimeupdate: ((Event) -> dynamic)?
    override var ontoggle: ((Event) -> dynamic)?
    override var onvolumechange: ((Event) -> dynamic)?
    override var onwaiting: ((Event) -> dynamic)?
    override var onwheel: ((WheelEvent) -> dynamic)?
    override val previousElementSibling: Element
    override val style: CSSStyleDeclaration
    open fun after(nodes: Array)
    open fun append(nodes: Array)
    open fun before(nodes: Array)
    open fun convertPointFromNode(point: DOMPointInit, from: dynamic, options: ConvertCoordinateOptions): DOMPoint
    open fun convertQuadFromNode(quad: dynamic, from: dynamic, options: ConvertCoordinateOptions): DOMQuad
    open fun convertRectFromNode(rect: DOMRectReadOnly, from: dynamic, options: ConvertCoordinateOptions): DOMQuad
    open fun getBoxQuads(options: BoxQuadOptions): Array
    open fun prepend(nodes: Array)
    open fun querySelector(selectors: String): Element
    open fun querySelectorAll(selectors: String): NodeList
    open fun remove()
    open fun replaceWith(nodes: Array)
}

public external open class PageTransitionEvent : Event {
    open val persisted: Boolean
}

public external interface PageTransitionEventInit : EventInit

public external interface ParentNode

public external open class Path2D : CanvasPath {
    open fun addPath(path: Path2D, transform: dynamic)
    open fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean)
    open fun arcTo(x1: Double, y1: Double, x2: Double, y2: Double, radius: Double)
    open fun arcTo(x1: Double, y1: Double, x2: Double, y2: Double, radiusX: Double, radiusY: Double, rotation: Double)
    open fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double)
    open fun closePath()
    open fun ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean)
    open fun lineTo(x: Double, y: Double)
    open fun moveTo(x: Double, y: Double)
    open fun quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double)
    open fun rect(x: Double, y: Double, w: Double, h: Double)
}

public external abstract class Plugin : ItemArrayLike<MimeType> {
    open val description: String
    open val filename: String
    open val name: String
    open fun item(index: Int): MimeType
    open fun namedItem(name: String): MimeType
}

public external abstract class PluginArray : ItemArrayLike<Plugin> {
    open fun item(index: Int): Plugin
    open fun namedItem(name: String): Plugin
    open fun refresh(reload: Boolean)
}

public external open class PopStateEvent : Event {
    open val state: Any
}

public external interface PopStateEventInit : EventInit

public external interface PremultiplyAlpha

public external abstract class ProcessingInstruction : CharacterData, LinkStyle, UnionElementOrProcessingInstruction {
    open val target: String
}

public external open class PromiseRejectionEvent : Event {
    open val promise: Promise
    open val reason: Any
}

public external interface PromiseRejectionEventInit : EventInit

public external abstract class RadioNodeList : NodeList, UnionElementOrRadioNodeList {
    open var value: String
}

public external open class Range {
    open val collapsed: Boolean
    open val commonAncestorContainer: Node
    open val endContainer: Node
    open val endOffset: Int
    open val startContainer: Node
    open val startOffset: Int
    open fun cloneContents(): DocumentFragment
    open fun cloneRange(): Range
    open fun collapse(toStart: Boolean)
    open fun compareBoundaryPoints(how: Short, sourceRange: Range): Short
    open fun comparePoint(node: Node, offset: Int): Short
    open fun createContextualFragment(fragment: String): DocumentFragment
    open fun deleteContents()
    open fun detach()
    open fun extractContents(): DocumentFragment
    open fun getBoundingClientRect(): DOMRect
    open fun getClientRects(): Array
    open fun insertNode(node: Node)
    open fun intersectsNode(node: Node): Boolean
    open fun isPointInRange(node: Node, offset: Int): Boolean
    open fun selectNode(node: Node)
    open fun selectNodeContents(node: Node)
    open fun setEnd(node: Node, offset: Int)
    open fun setEndAfter(node: Node)
    open fun setEndBefore(node: Node)
    open fun setStart(node: Node, offset: Int)
    open fun setStartAfter(node: Node)
    open fun setStartBefore(node: Node)
    open fun surroundContents(newParent: Node)
}

public external open class RelatedEvent : Event {
    open val relatedTarget: EventTarget
}

public external interface RelatedEventInit : EventInit

public external interface RenderingContext

public external interface ResizeQuality

public external abstract class Screen {
    open val availHeight: Int
    open val availWidth: Int
    open val colorDepth: Int
    open val height: Int
    open val pixelDepth: Int
    open val width: Int
}

public external interface ScrollBehavior

public external interface ScrollIntoViewOptions : ScrollOptions

public external interface ScrollLogicalPosition

public external interface ScrollOptions

public external interface ScrollRestoration

public external interface ScrollToOptions : ScrollOptions

public external interface SelectionMode

public external open class ShadowRoot : DocumentFragment, DocumentOrShadowRoot {
    override val fullscreenElement: Element
    open val host: Element
    open val mode: ShadowRootMode
}

public external interface ShadowRootInit

public external interface ShadowRootMode

public external open class SharedWorker : EventTarget, AbstractWorker {
    override var onerror: ((Event) -> dynamic)?
    open val port: MessagePort
}

public external abstract class SharedWorkerGlobalScope : WorkerGlobalScope {
    open val applicationCache: ApplicationCache
    open val name: String
    open var onconnect: ((Event) -> dynamic)?
    open fun close()
}

public external interface Slotable

public external abstract class Storage {
    open val length: Int
    open fun clear()
    open fun getItem(key: String): String
    open fun key(index: Int): String
    open fun removeItem(key: String)
    open fun setItem(key: String, value: String)
}

public external open class StorageEvent : Event {
    open val key: String
    open val newValue: String
    open val oldValue: String
    open val storageArea: Storage
    open val url: String
}

public external interface StorageEventInit : EventInit

public external open class Text : CharacterData, Slotable, GeometryUtils {
    override val assignedSlot: HTMLSlotElement
    override val nextElementSibling: Element
    override val previousElementSibling: Element
    open val wholeText: String
    open fun after(nodes: Array)
    open fun before(nodes: Array)
    open fun convertPointFromNode(point: DOMPointInit, from: dynamic, options: ConvertCoordinateOptions): DOMPoint
    open fun convertQuadFromNode(quad: dynamic, from: dynamic, options: ConvertCoordinateOptions): DOMQuad
    open fun convertRectFromNode(rect: DOMRectReadOnly, from: dynamic, options: ConvertCoordinateOptions): DOMQuad
    open fun getBoxQuads(options: BoxQuadOptions): Array
    open fun remove()
    open fun replaceWith(nodes: Array)
    open fun splitText(offset: Int): Text
}

public external abstract class TextMetrics {
    open val actualBoundingBoxAscent: Double
    open val actualBoundingBoxDescent: Double
    open val actualBoundingBoxLeft: Double
    open val actualBoundingBoxRight: Double
    open val alphabeticBaseline: Double
    open val emHeightAscent: Double
    open val emHeightDescent: Double
    open val fontBoundingBoxAscent: Double
    open val fontBoundingBoxDescent: Double
    open val hangingBaseline: Double
    open val ideographicBaseline: Double
    open val width: Double
}

public external abstract class TextTrack : EventTarget, UnionAudioTrackOrTextTrackOrVideoTrack {
    open val activeCues: TextTrackCueList
    open val cues: TextTrackCueList
    open val id: String
    open val inBandMetadataTrackDispatchType: String
    open val kind: TextTrackKind
    open val label: String
    open val language: String
    open var mode: TextTrackMode
    open var oncuechange: ((Event) -> dynamic)?
    open val sourceBuffer: SourceBuffer
    open fun addCue(cue: TextTrackCue)
    open fun removeCue(cue: TextTrackCue)
}

public external abstract class TextTrackCue : EventTarget {
    open var endTime: Double
    open var id: String
    open var onenter: ((Event) -> dynamic)?
    open var onexit: ((Event) -> dynamic)?
    open var pauseOnExit: Boolean
    open var startTime: Double
    open val track: TextTrack
}

public external abstract class TextTrackCueList {
    open val length: Int
    open fun getCueById(id: String): TextTrackCue
}

public external interface TextTrackKind

public external abstract class TextTrackList : EventTarget {
    open val length: Int
    open var onaddtrack: ((TrackEvent) -> dynamic)?
    open var onchange: ((Event) -> dynamic)?
    open var onremovetrack: ((TrackEvent) -> dynamic)?
    open fun getTrackById(id: String): TextTrack
}

public external interface TextTrackMode

public external abstract class TimeRanges {
    open val length: Int
    open fun end(index: Int): Double
    open fun start(index: Int): Double
}

public external abstract class Touch {
    open val clientX: Int
    open val clientY: Int
    open val identifier: Int
    open val pageX: Int
    open val pageY: Int
    open val region: String
    open val screenX: Int
    open val screenY: Int
    open val target: EventTarget
}

public external open class TouchEvent : UIEvent {
    open val altKey: Boolean
    open val changedTouches: TouchList
    open val ctrlKey: Boolean
    open val metaKey: Boolean
    open val shiftKey: Boolean
    open val targetTouches: TouchList
    open val touches: TouchList
}

public external abstract class TouchList : ItemArrayLike<Touch> {
    open fun item(index: Int): Touch
}

public external open class TrackEvent : Event {
    open val track: UnionAudioTrackOrTextTrackOrVideoTrack
}

public external interface TrackEventInit : EventInit

public external abstract class TreeWalker {
    open var currentNode: Node
    open val filter: NodeFilter
    open val root: Node
    open val whatToShow: Int
    open fun firstChild(): Node
    open fun lastChild(): Node
    open fun nextNode(): Node
    open fun nextSibling(): Node
    open fun parentNode(): Node
    open fun previousNode(): Node
    open fun previousSibling(): Node
}

public external interface UnionAudioTrackOrTextTrackOrVideoTrack

public external interface UnionElementOrHTMLCollection

public external interface UnionElementOrMouseEvent

public external interface UnionElementOrRadioNodeList

public external interface UnionHTMLOptGroupElementOrHTMLOptionElement

public external interface UnionMessagePortOrWindowProxy

public external abstract class ValidityState {
    open val badInput: Boolean
    open val customError: Boolean
    open val patternMismatch: Boolean
    open val rangeOverflow: Boolean
    open val rangeUnderflow: Boolean
    open val stepMismatch: Boolean
    open val tooLong: Boolean
    open val tooShort: Boolean
    open val typeMismatch: Boolean
    open val valid: Boolean
    open val valueMissing: Boolean
}

public external abstract class VideoTrack : UnionAudioTrackOrTextTrackOrVideoTrack {
    open val id: String
    open val kind: String
    open val label: String
    open val language: String
    open var selected: Boolean
    open val sourceBuffer: SourceBuffer
}

public external abstract class VideoTrackList : EventTarget {
    open val length: Int
    open var onaddtrack: ((TrackEvent) -> dynamic)?
    open var onchange: ((Event) -> dynamic)?
    open var onremovetrack: ((TrackEvent) -> dynamic)?
    open val selectedIndex: Int
    open fun getTrackById(id: String): VideoTrack
}

public external open class WebSocket : EventTarget {
    open var binaryType: BinaryType
    open val bufferedAmount: Number
    open val extensions: String
    open var onclose: ((Event) -> dynamic)?
    open var onerror: ((Event) -> dynamic)?
    open var onmessage: ((MessageEvent) -> dynamic)?
    open var onopen: ((Event) -> dynamic)?
    open val protocol: String
    open val readyState: Short
    open val url: String
    open fun close(code: Short, reason: String)
    open fun send(data: String)
    open fun send(data: ArrayBuffer)
    open fun send(data: ArrayBufferView)
    open fun send(data: Blob)
}

public external abstract class Window : EventTarget, GlobalEventHandlers, WindowEventHandlers, WindowOrWorkerGlobalScope, WindowSessionStorage, WindowLocalStorage, GlobalPerformance, UnionMessagePortOrWindowProxy {
    open val applicationCache: ApplicationCache
    open val closed: Boolean
    open val customElements: CustomElementRegistry
    open val devicePixelRatio: Double
    open val document: Document
    open val external: External
    open val frameElement: Element
    open val frames: Window
    open val history: History
    open val innerHeight: Int
    open val innerWidth: Int
    open val length: Int
    open val location: Location
    open val locationbar: BarProp
    open val menubar: BarProp
    open var name: String
    open val navigator: Navigator
    open var opener: Any
    open val outerHeight: Int
    open val outerWidth: Int
    open val pageXOffset: Double
    open val pageYOffset: Double
    open val parent: Window
    open val personalbar: BarProp
    open val screen: Screen
    open val screenX: Int
    open val screenY: Int
    open val scrollX: Double
    open val scrollY: Double
    open val scrollbars: BarProp
    open val self: Window
    open var status: String
    open val statusbar: BarProp
    open val toolbar: BarProp
    open val top: Window
    open val window: Window
    open fun alert()
    open fun alert(message: String)
    open fun blur()
    open fun cancelAnimationFrame(handle: Int)
    open fun captureEvents()
    open fun close()
    open fun confirm(message: String): Boolean
    open fun focus()
    open fun getComputedStyle(elt: Element, pseudoElt: String): CSSStyleDeclaration
    open fun matchMedia(query: String): MediaQueryList
    open fun moveBy(x: Int, y: Int)
    open fun moveTo(x: Int, y: Int)
    open fun open(url: String, target: String, features: String): Window
    open fun postMessage(message: Any, targetOrigin: String, transfer: Array)
    open fun print()
    open fun prompt(message: String, default: String): String
    open fun releaseEvents()
    open fun requestAnimationFrame(callback: ((Double) -> Unit)?): Int
    open fun resizeBy(x: Int, y: Int)
    open fun resizeTo(x: Int, y: Int)
    open fun scroll(x: Double, y: Double)
    open fun scroll(options: ScrollToOptions)
    open fun scrollBy(x: Double, y: Double)
    open fun scrollBy(options: ScrollToOptions)
    open fun scrollTo(x: Double, y: Double)
    open fun scrollTo(options: ScrollToOptions)
    open fun stop()
}

public external interface WindowEventHandlers

public external interface WindowLocalStorage

public external interface WindowOrWorkerGlobalScope

public external interface WindowSessionStorage

public external open class Worker : EventTarget, AbstractWorker {
    override var onerror: ((Event) -> dynamic)?
    open var onmessage: ((MessageEvent) -> dynamic)?
    open fun postMessage(message: Any, transfer: Array)
    open fun terminate()
}

public external abstract class WorkerGlobalScope : EventTarget, WindowOrWorkerGlobalScope, GlobalPerformance {
    open val location: WorkerLocation
    open val navigator: WorkerNavigator
    open var onerror: Function5
    open var onlanguagechange: ((Event) -> dynamic)?
    open var onoffline: ((Event) -> dynamic)?
    open var ononline: ((Event) -> dynamic)?
    open var onrejectionhandled: ((Event) -> dynamic)?
    open var onunhandledrejection: ((PromiseRejectionEvent) -> dynamic)?
    open val self: WorkerGlobalScope
    open fun importScripts(urls: Array)
}

public external abstract class WorkerLocation {
    open val hash: String
    open val host: String
    open val hostname: String
    open val href: String
    open val origin: String
    open val pathname: String
    open val port: String
    open val protocol: String
    open val search: String
}

public external abstract class WorkerNavigator : NavigatorID, NavigatorLanguage, NavigatorOnLine, NavigatorConcurrentHardware {
    open val serviceWorker: ServiceWorkerContainer
}

public external interface WorkerOptions

public external interface WorkerType

public external open class XMLDocument : Document
// --------- org.w3c.dom.events ---------
package org.w3c.dom.events


public external open class CompositionEvent : UIEvent {
    open val data: String
}

public external interface CompositionEventInit : UIEventInit

public external open class Event {
    open val bubbles: Boolean
    open val cancelable: Boolean
    open val composed: Boolean
    open val currentTarget: EventTarget
    open val defaultPrevented: Boolean
    open val eventPhase: Short
    open val isTrusted: Boolean
    open val target: EventTarget
    open val timeStamp: Number
    open val type: String
    open fun composedPath(): Array
    open fun initEvent(type: String, bubbles: Boolean, cancelable: Boolean)
    open fun preventDefault()
    open fun stopImmediatePropagation()
    open fun stopPropagation()
}

public external interface EventListener

public class EventListenerHandler : EventListener {
    open val handler: ((Event) -> Unit)?
    open fun handleEvent(event: Event)
    open fun toString(): String
}

public external interface EventModifierInit : UIEventInit

public external abstract class EventTarget {
    open fun addEventListener(type: String, callback: ((Event) -> Unit)?, options: dynamic)
    open fun addEventListener(type: String, callback: EventListener, options: dynamic)
    open fun dispatchEvent(event: Event): Boolean
    open fun removeEventListener(type: String, callback: ((Event) -> Unit)?, options: dynamic)
    open fun removeEventListener(type: String, callback: EventListener, options: dynamic)
}

public external open class FocusEvent : UIEvent {
    open val relatedTarget: EventTarget
}

public external interface FocusEventInit : UIEventInit

public external open class InputEvent : UIEvent {
    open val data: String
    open val isComposing: Boolean
}

public external interface InputEventInit : UIEventInit

public external open class KeyboardEvent : UIEvent {
    open val altKey: Boolean
    open val charCode: Int
    open val code: String
    open val ctrlKey: Boolean
    open val isComposing: Boolean
    open val key: String
    open val keyCode: Int
    open val location: Int
    open val metaKey: Boolean
    open val repeat: Boolean
    open val shiftKey: Boolean
    open val which: Int
    open fun getModifierState(keyArg: String): Boolean
}

public external interface KeyboardEventInit : EventModifierInit

public external open class MouseEvent : UIEvent, UnionElementOrMouseEvent {
    open val altKey: Boolean
    open val button: Short
    open val buttons: Short
    open val clientX: Int
    open val clientY: Int
    open val ctrlKey: Boolean
    open val metaKey: Boolean
    open val offsetX: Double
    open val offsetY: Double
    open val pageX: Double
    open val pageY: Double
    open val region: String
    open val relatedTarget: EventTarget
    open val screenX: Int
    open val screenY: Int
    open val shiftKey: Boolean
    open val x: Double
    open val y: Double
    open fun getModifierState(keyArg: String): Boolean
}

public external interface MouseEventInit : EventModifierInit

public external open class UIEvent : Event {
    open val detail: Int
    open val view: Window
}

public external interface UIEventInit : EventInit

public external open class WheelEvent : MouseEvent {
    open val deltaMode: Int
    open val deltaX: Double
    open val deltaY: Double
    open val deltaZ: Double
}

public external interface WheelEventInit : MouseEventInit
// --------- org.w3c.dom.parsing ---------
package org.w3c.dom.parsing


public external open class DOMParser {
    open fun parseFromString(str: String, type: dynamic): Document
}

public external open class XMLSerializer {
    open fun serializeToString(root: Node): String
}
// --------- org.w3c.dom.svg ---------
package org.w3c.dom.svg


public external interface GetSVGDocument

public external abstract class SVGAElement : SVGGraphicsElement, SVGURIReference {
    open val download: SVGAnimatedString
    open val hreflang: SVGAnimatedString
    open val rel: SVGAnimatedString
    open val relList: SVGAnimatedString
    open val target: SVGAnimatedString
    open val type: SVGAnimatedString
}

public external abstract class SVGAngle {
    open val unitType: Short
    open var value: Float
    open var valueAsString: String
    open var valueInSpecifiedUnits: Float
    open fun convertToSpecifiedUnits(unitType: Short)
    open fun newValueSpecifiedUnits(unitType: Short, valueInSpecifiedUnits: Float)
}

public external abstract class SVGAnimatedAngle {
    open val animVal: SVGAngle
    open val baseVal: SVGAngle
}

public external abstract class SVGAnimatedBoolean {
    open val animVal: Boolean
    open var baseVal: Boolean
}

public external abstract class SVGAnimatedEnumeration {
    open val animVal: Short
    open var baseVal: Short
}

public external abstract class SVGAnimatedInteger {
    open val animVal: Int
    open var baseVal: Int
}

public external abstract class SVGAnimatedLength {
    open val animVal: SVGLength
    open val baseVal: SVGLength
}

public external abstract class SVGAnimatedLengthList {
    open val animVal: SVGLengthList
    open val baseVal: SVGLengthList
}

public external abstract class SVGAnimatedNumber {
    open val animVal: Float
    open var baseVal: Float
}

public external abstract class SVGAnimatedNumberList {
    open val animVal: SVGNumberList
    open val baseVal: SVGNumberList
}

public external interface SVGAnimatedPoints

public external abstract class SVGAnimatedPreserveAspectRatio {
    open val animVal: SVGPreserveAspectRatio
    open val baseVal: SVGPreserveAspectRatio
}

public external abstract class SVGAnimatedRect {
    open val animVal: DOMRectReadOnly
    open val baseVal: DOMRect
}

public external abstract class SVGAnimatedString {
    open val animVal: String
    open var baseVal: String
}

public external abstract class SVGAnimatedTransformList {
    open val animVal: SVGTransformList
    open val baseVal: SVGTransformList
}

public external interface SVGBoundingBoxOptions

public external abstract class SVGCircleElement : SVGGeometryElement {
    open val cx: SVGAnimatedLength
    open val cy: SVGAnimatedLength
    open val r: SVGAnimatedLength
}

public external abstract class SVGCursorElement : SVGElement, SVGURIReference {
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
}

public external abstract class SVGDefsElement : SVGGraphicsElement

public external abstract class SVGDescElement : SVGElement

public external abstract class SVGElement : Element, ElementCSSInlineStyle, GlobalEventHandlers, SVGElementInstance {
    open val dataset: DOMStringMap
    open val ownerSVGElement: SVGSVGElement
    open var tabIndex: Int
    open val viewportElement: SVGElement
    open fun blur()
    open fun focus()
}

public external interface SVGElementInstance

public external abstract class SVGEllipseElement : SVGGeometryElement {
    open val cx: SVGAnimatedLength
    open val cy: SVGAnimatedLength
    open val rx: SVGAnimatedLength
    open val ry: SVGAnimatedLength
}

public external interface SVGFitToViewBox

public external abstract class SVGForeignObjectElement : SVGGraphicsElement {
    open val height: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
}

public external abstract class SVGGElement : SVGGraphicsElement

public external abstract class SVGGeometryElement : SVGGraphicsElement {
    open val pathLength: SVGAnimatedNumber
    open fun getPointAtLength(distance: Float): DOMPoint
    open fun getTotalLength(): Float
    open fun isPointInFill(point: DOMPoint): Boolean
    open fun isPointInStroke(point: DOMPoint): Boolean
}

public external abstract class SVGGradientElement : SVGElement, SVGURIReference, SVGUnitTypes {
    open val gradientTransform: SVGAnimatedTransformList
    open val gradientUnits: SVGAnimatedEnumeration
    open val spreadMethod: SVGAnimatedEnumeration
}

public external abstract class SVGGraphicsElement : SVGElement, SVGTests {
    open val transform: SVGAnimatedTransformList
    open fun getBBox(options: SVGBoundingBoxOptions): DOMRect
    open fun getCTM(): DOMMatrix
    open fun getScreenCTM(): DOMMatrix
}

public external abstract class SVGHatchElement : SVGElement

public external abstract class SVGHatchpathElement : SVGElement

public external abstract class SVGImageElement : SVGGraphicsElement, SVGURIReference, HTMLOrSVGImageElement {
    open var crossOrigin: String
    open val height: SVGAnimatedLength
    open val preserveAspectRatio: SVGAnimatedPreserveAspectRatio
    open val width: SVGAnimatedLength
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
}

public external abstract class SVGLength {
    open val unitType: Short
    open var value: Float
    open var valueAsString: String
    open var valueInSpecifiedUnits: Float
    open fun convertToSpecifiedUnits(unitType: Short)
    open fun newValueSpecifiedUnits(unitType: Short, valueInSpecifiedUnits: Float)
}

public external abstract class SVGLengthList {
    open val length: Int
    open val numberOfItems: Int
    open fun appendItem(newItem: SVGLength): SVGLength
    open fun clear()
    open fun getItem(index: Int): SVGLength
    open fun initialize(newItem: SVGLength): SVGLength
    open fun insertItemBefore(newItem: SVGLength, index: Int): SVGLength
    open fun removeItem(index: Int): SVGLength
    open fun replaceItem(newItem: SVGLength, index: Int): SVGLength
}

public external abstract class SVGLineElement : SVGGeometryElement {
    open val x1: SVGAnimatedLength
    open val x2: SVGAnimatedLength
    open val y1: SVGAnimatedLength
    open val y2: SVGAnimatedLength
}

public external abstract class SVGLinearGradientElement : SVGGradientElement {
    open val x1: SVGAnimatedLength
    open val x2: SVGAnimatedLength
    open val y1: SVGAnimatedLength
    open val y2: SVGAnimatedLength
}

public external abstract class SVGMarkerElement : SVGElement, SVGFitToViewBox {
    open val markerHeight: SVGAnimatedLength
    open val markerUnits: SVGAnimatedEnumeration
    open val markerWidth: SVGAnimatedLength
    open var orient: String
    open val orientAngle: SVGAnimatedAngle
    open val orientType: SVGAnimatedEnumeration
    open val refX: SVGAnimatedLength
    open val refY: SVGAnimatedLength
    open fun setOrientToAngle(angle: SVGAngle)
    open fun setOrientToAuto()
}

public external abstract class SVGMeshElement : SVGGeometryElement, SVGURIReference

public external abstract class SVGMeshGradientElement : SVGGradientElement

public external abstract class SVGMeshpatchElement : SVGElement

public external abstract class SVGMeshrowElement : SVGElement

public external abstract class SVGMetadataElement : SVGElement

public external abstract class SVGNameList {
    open val length: Int
    open val numberOfItems: Int
    open fun appendItem(newItem: dynamic): dynamic
    open fun clear()
    open fun getItem(index: Int): dynamic
    open fun initialize(newItem: dynamic): dynamic
    open fun insertItemBefore(newItem: dynamic, index: Int): dynamic
    open fun removeItem(index: Int): dynamic
    open fun replaceItem(newItem: dynamic, index: Int): dynamic
}

public external abstract class SVGNumber {
    open var value: Float
}

public external abstract class SVGNumberList {
    open val length: Int
    open val numberOfItems: Int
    open fun appendItem(newItem: SVGNumber): SVGNumber
    open fun clear()
    open fun getItem(index: Int): SVGNumber
    open fun initialize(newItem: SVGNumber): SVGNumber
    open fun insertItemBefore(newItem: SVGNumber, index: Int): SVGNumber
    open fun removeItem(index: Int): SVGNumber
    open fun replaceItem(newItem: SVGNumber, index: Int): SVGNumber
}

public external abstract class SVGPathElement : SVGGeometryElement

public external abstract class SVGPatternElement : SVGElement, SVGFitToViewBox, SVGURIReference, SVGUnitTypes {
    open val height: SVGAnimatedLength
    open val patternContentUnits: SVGAnimatedEnumeration
    open val patternTransform: SVGAnimatedTransformList
    open val patternUnits: SVGAnimatedEnumeration
    open val width: SVGAnimatedLength
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
}

public external abstract class SVGPointList {
    open val length: Int
    open val numberOfItems: Int
    open fun appendItem(newItem: DOMPoint): DOMPoint
    open fun clear()
    open fun getItem(index: Int): DOMPoint
    open fun initialize(newItem: DOMPoint): DOMPoint
    open fun insertItemBefore(newItem: DOMPoint, index: Int): DOMPoint
    open fun removeItem(index: Int): DOMPoint
    open fun replaceItem(newItem: DOMPoint, index: Int): DOMPoint
}

public external abstract class SVGPolygonElement : SVGGeometryElement, SVGAnimatedPoints

public external abstract class SVGPolylineElement : SVGGeometryElement, SVGAnimatedPoints

public external abstract class SVGPreserveAspectRatio {
    open var align: Short
    open var meetOrSlice: Short
}

public external abstract class SVGRadialGradientElement : SVGGradientElement {
    open val cx: SVGAnimatedLength
    open val cy: SVGAnimatedLength
    open val fr: SVGAnimatedLength
    open val fx: SVGAnimatedLength
    open val fy: SVGAnimatedLength
    open val r: SVGAnimatedLength
}

public external abstract class SVGRectElement : SVGGeometryElement {
    open val height: SVGAnimatedLength
    open val rx: SVGAnimatedLength
    open val ry: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
}

public external abstract class SVGSVGElement : SVGGraphicsElement, SVGFitToViewBox, SVGZoomAndPan, WindowEventHandlers {
    open var currentScale: Float
    open val currentTranslate: DOMPointReadOnly
    open val height: SVGAnimatedLength
    open val width: SVGAnimatedLength
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
    open fun checkEnclosure(element: SVGElement, rect: DOMRectReadOnly): Boolean
    open fun checkIntersection(element: SVGElement, rect: DOMRectReadOnly): Boolean
    open fun createSVGAngle(): SVGAngle
    open fun createSVGLength(): SVGLength
    open fun createSVGMatrix(): DOMMatrix
    open fun createSVGNumber(): SVGNumber
    open fun createSVGPoint(): DOMPoint
    open fun createSVGRect(): DOMRect
    open fun createSVGTransform(): SVGTransform
    open fun createSVGTransformFromMatrix(matrix: DOMMatrixReadOnly): SVGTransform
    open fun deselectAll()
    open fun forceRedraw()
    open fun getElementById(elementId: String): Element
    open fun getEnclosureList(rect: DOMRectReadOnly, referenceElement: SVGElement): NodeList
    open fun getIntersectionList(rect: DOMRectReadOnly, referenceElement: SVGElement): NodeList
    open fun suspendRedraw(maxWaitMilliseconds: Int): Int
    open fun unsuspendRedraw(suspendHandleID: Int)
    open fun unsuspendRedrawAll()
}

public external abstract class SVGScriptElement : SVGElement, SVGURIReference, HTMLOrSVGScriptElement {
    open var crossOrigin: String
    open var type: String
}

public external abstract class SVGSolidcolorElement : SVGElement

public external abstract class SVGStopElement : SVGElement {
    open val offset: SVGAnimatedNumber
}

public external abstract class SVGStringList {
    open val length: Int
    open val numberOfItems: Int
    open fun appendItem(newItem: String): String
    open fun clear()
    open fun getItem(index: Int): String
    open fun initialize(newItem: String): String
    open fun insertItemBefore(newItem: String, index: Int): String
    open fun removeItem(index: Int): String
    open fun replaceItem(newItem: String, index: Int): String
}

public external abstract class SVGStyleElement : SVGElement, LinkStyle {
    open var media: String
    open var title: String
    open var type: String
}

public external abstract class SVGSwitchElement : SVGGraphicsElement

public external abstract class SVGSymbolElement : SVGGraphicsElement, SVGFitToViewBox

public external abstract class SVGTSpanElement : SVGTextPositioningElement

public external interface SVGTests

public external abstract class SVGTextContentElement : SVGGraphicsElement {
    open val lengthAdjust: SVGAnimatedEnumeration
    open val textLength: SVGAnimatedLength
    open fun getCharNumAtPosition(point: DOMPoint): Int
    open fun getComputedTextLength(): Float
    open fun getEndPositionOfChar(charnum: Int): DOMPoint
    open fun getExtentOfChar(charnum: Int): DOMRect
    open fun getNumberOfChars(): Int
    open fun getRotationOfChar(charnum: Int): Float
    open fun getStartPositionOfChar(charnum: Int): DOMPoint
    open fun getSubStringLength(charnum: Int, nchars: Int): Float
    open fun selectSubString(charnum: Int, nchars: Int)
}

public external abstract class SVGTextElement : SVGTextPositioningElement

public external abstract class SVGTextPathElement : SVGTextContentElement, SVGURIReference {
    open val method: SVGAnimatedEnumeration
    open val spacing: SVGAnimatedEnumeration
    open val startOffset: SVGAnimatedLength
}

public external abstract class SVGTextPositioningElement : SVGTextContentElement {
    open val dx: SVGAnimatedLengthList
    open val dy: SVGAnimatedLengthList
    open val rotate: SVGAnimatedNumberList
    open val x: SVGAnimatedLengthList
    open val y: SVGAnimatedLengthList
}

public external abstract class SVGTitleElement : SVGElement

public external abstract class SVGTransform {
    open val angle: Float
    open val matrix: DOMMatrix
    open val type: Short
    open fun setMatrix(matrix: DOMMatrixReadOnly)
    open fun setRotate(angle: Float, cx: Float, cy: Float)
    open fun setScale(sx: Float, sy: Float)
    open fun setSkewX(angle: Float)
    open fun setSkewY(angle: Float)
    open fun setTranslate(tx: Float, ty: Float)
}

public external abstract class SVGTransformList {
    open val length: Int
    open val numberOfItems: Int
    open fun appendItem(newItem: SVGTransform): SVGTransform
    open fun clear()
    open fun consolidate(): SVGTransform
    open fun createSVGTransformFromMatrix(matrix: DOMMatrixReadOnly): SVGTransform
    open fun getItem(index: Int): SVGTransform
    open fun initialize(newItem: SVGTransform): SVGTransform
    open fun insertItemBefore(newItem: SVGTransform, index: Int): SVGTransform
    open fun removeItem(index: Int): SVGTransform
    open fun replaceItem(newItem: SVGTransform, index: Int): SVGTransform
}

public external interface SVGURIReference

public external interface SVGUnitTypes

public external abstract class SVGUnknownElement : SVGGraphicsElement

public external abstract class SVGUseElement : SVGGraphicsElement, SVGURIReference {
    open val animatedInstanceRoot: SVGElement
    open val height: SVGAnimatedLength
    open val instanceRoot: SVGElement
    open val width: SVGAnimatedLength
    open val x: SVGAnimatedLength
    open val y: SVGAnimatedLength
}

public external open class SVGUseElementShadowRoot : ShadowRoot

public external abstract class SVGViewElement : SVGElement, SVGFitToViewBox, SVGZoomAndPan

public external interface SVGZoomAndPan

public external open class ShadowAnimation {
    open val sourceAnimation: dynamic
}
// --------- org.w3c.dom.url ---------
package org.w3c.dom.url


public external open class URL {
    open var hash: String
    open var host: String
    open var hostname: String
    open var href: String
    open val origin: String
    open var password: String
    open var pathname: String
    open var port: String
    open var protocol: String
    open var search: String
    open val searchParams: URLSearchParams
    open var username: String
}

public external open class URLSearchParams {
    open fun append(name: String, value: String)
    open fun delete(name: String)
    open fun get(name: String): String
    open fun getAll(name: String): Array
    open fun has(name: String): Boolean
    open fun set(name: String, value: String)
}
// --------- org.w3c.fetch ---------
package org.w3c.fetch


public external interface Body

public external open class Headers {
    open fun append(name: String, value: String)
    open fun delete(name: String)
    open fun get(name: String): String
    open fun has(name: String): Boolean
    open fun set(name: String, value: String)
}

public external open class Request : Body {
    override val bodyUsed: Boolean
    open val cache: RequestCache
    open val credentials: RequestCredentials
    open val destination: RequestDestination
    open val headers: Headers
    open val integrity: String
    open val keepalive: Boolean
    open val method: String
    open val mode: RequestMode
    open val redirect: RequestRedirect
    open val referrer: String
    open val referrerPolicy: dynamic
    open val type: RequestType
    open val url: String
    open fun arrayBuffer(): Promise
    open fun blob(): Promise
    open fun clone(): Request
    open fun formData(): Promise
    open fun json(): Promise
    open fun text(): Promise
}

public external interface RequestCache

public external interface RequestCredentials

public external interface RequestDestination

public external interface RequestInit

public external interface RequestMode

public external interface RequestRedirect

public external interface RequestType

public external open class Response : Body {
    open val body: dynamic
    override val bodyUsed: Boolean
    open val headers: Headers
    open val ok: Boolean
    open val redirected: Boolean
    open val status: Short
    open val statusText: String
    open val trailer: Promise
    open val type: ResponseType
    open val url: String
    open fun arrayBuffer(): Promise
    open fun blob(): Promise
    open fun clone(): Response
    open fun formData(): Promise
    open fun json(): Promise
    open fun text(): Promise
}

public external interface ResponseInit

public external interface ResponseType
// --------- org.w3c.files ---------
package org.w3c.files


public external open class Blob : MediaProvider, ImageBitmapSource {
    open val isClosed: Boolean
    open val size: Number
    open val type: String
    open fun close()
    open fun slice(start: Int, end: Int, contentType: String): Blob
}

public external interface BlobPropertyBag

public external open class File : Blob {
    open val lastModified: Int
    open val name: String
}

public external abstract class FileList : ItemArrayLike<File> {
    open fun item(index: Int): File
}

public external interface FilePropertyBag : BlobPropertyBag

public external open class FileReader : EventTarget {
    open val error: dynamic
    open var onabort: ((Event) -> dynamic)?
    open var onerror: ((Event) -> dynamic)?
    open var onload: ((Event) -> dynamic)?
    open var onloadend: ((Event) -> dynamic)?
    open var onloadstart: ((ProgressEvent) -> dynamic)?
    open var onprogress: ((ProgressEvent) -> dynamic)?
    open val readyState: Short
    open val result: dynamic
    open fun abort()
    open fun readAsArrayBuffer(blob: Blob)
    open fun readAsBinaryString(blob: Blob)
    open fun readAsDataURL(blob: Blob)
    open fun readAsText(blob: Blob, label: String)
}

public external open class FileReaderSync {
    open fun readAsArrayBuffer(blob: Blob): ArrayBuffer
    open fun readAsBinaryString(blob: Blob): String
    open fun readAsDataURL(blob: Blob): String
    open fun readAsText(blob: Blob, label: String): String
}
// --------- org.w3c.notifications ---------
package org.w3c.notifications


public external interface GetNotificationOptions

public external open class Notification : EventTarget {
    open val actions: Array
    open val badge: String
    open val body: String
    open val data: Any
    open val dir: NotificationDirection
    open val icon: String
    open val image: String
    open val lang: String
    open val noscreen: Boolean
    open var onclick: ((MouseEvent) -> dynamic)?
    open var onerror: ((Event) -> dynamic)?
    open val renotify: Boolean
    open val requireInteraction: Boolean
    open val silent: Boolean
    open val sound: String
    open val sticky: Boolean
    open val tag: String
    open val timestamp: Number
    open val title: String
    open val vibrate: Array
    open fun close()
}

public external interface NotificationAction

public external interface NotificationDirection

public external open class NotificationEvent : ExtendableEvent {
    open val action: String
    open val notification: Notification
}

public external interface NotificationEventInit : ExtendableEventInit

public external interface NotificationOptions

public external interface NotificationPermission
// --------- org.w3c.performance ---------
package org.w3c.performance


public external interface GlobalPerformance

public external abstract class Performance : EventTarget {
    open val navigation: PerformanceNavigation
    open val timing: PerformanceTiming
    open fun now(): Double
}

public external abstract class PerformanceNavigation {
    open val redirectCount: Short
    open val type: Short
}

public external abstract class PerformanceTiming {
    open val connectEnd: Number
    open val connectStart: Number
    open val domComplete: Number
    open val domContentLoadedEventEnd: Number
    open val domContentLoadedEventStart: Number
    open val domInteractive: Number
    open val domLoading: Number
    open val domainLookupEnd: Number
    open val domainLookupStart: Number
    open val fetchStart: Number
    open val loadEventEnd: Number
    open val loadEventStart: Number
    open val navigationStart: Number
    open val redirectEnd: Number
    open val redirectStart: Number
    open val requestStart: Number
    open val responseEnd: Number
    open val responseStart: Number
    open val secureConnectionStart: Number
    open val unloadEventEnd: Number
    open val unloadEventStart: Number
}
// --------- org.w3c.workers ---------
package org.w3c.workers


public external abstract class Cache {
    open fun add(request: dynamic): Promise
    open fun addAll(requests: Array): Promise
    open fun delete(request: dynamic, options: CacheQueryOptions): Promise
    open fun keys(request: dynamic, options: CacheQueryOptions): Promise
    open fun match(request: dynamic, options: CacheQueryOptions): Promise
    open fun matchAll(request: dynamic, options: CacheQueryOptions): Promise
    open fun put(request: dynamic, response: Response): Promise
}

public external interface CacheBatchOperation

public external interface CacheQueryOptions

public external abstract class CacheStorage {
    open fun delete(cacheName: String): Promise
    open fun has(cacheName: String): Promise
    open fun keys(): Promise
    open fun match(request: dynamic, options: CacheQueryOptions): Promise
    open fun open(cacheName: String): Promise
}

public external abstract class Client : UnionClientOrMessagePortOrServiceWorker {
    open val frameType: FrameType
    open val id: String
    open val url: String
    open fun postMessage(message: Any, transfer: Array)
}

public external interface ClientQueryOptions

public external interface ClientType

public external abstract class Clients {
    open fun claim(): Promise
    open fun get(id: String): Promise
    open fun matchAll(options: ClientQueryOptions): Promise
    open fun openWindow(url: String): Promise
}

public external open class ExtendableEvent : Event {
    open fun waitUntil(f: Promise)
}

public external interface ExtendableEventInit : EventInit

public external open class ExtendableMessageEvent : ExtendableEvent {
    open val data: Any
    open val lastEventId: String
    open val origin: String
    open val ports: Array
    open val source: UnionClientOrMessagePortOrServiceWorker
}

public external interface ExtendableMessageEventInit : ExtendableEventInit

public external open class FetchEvent : ExtendableEvent {
    open val clientId: String
    open val isReload: Boolean
    open val request: Request
    open fun respondWith(r: Promise)
}

public external interface FetchEventInit : ExtendableEventInit

public external open class ForeignFetchEvent : ExtendableEvent {
    open val origin: String
    open val request: Request
    open fun respondWith(r: Promise)
}

public external interface ForeignFetchEventInit : ExtendableEventInit

public external interface ForeignFetchOptions

public external interface ForeignFetchResponse

public external interface FrameType

public external open class FunctionalEvent : ExtendableEvent

public external open class InstallEvent : ExtendableEvent {
    open fun registerForeignFetch(options: ForeignFetchOptions)
}

public external interface RegistrationOptions

public external abstract class ServiceWorker : EventTarget, AbstractWorker, UnionMessagePortOrServiceWorker, UnionClientOrMessagePortOrServiceWorker {
    open var onstatechange: ((Event) -> dynamic)?
    open val scriptURL: String
    open val state: ServiceWorkerState
    open fun postMessage(message: Any, transfer: Array)
}

public external abstract class ServiceWorkerContainer : EventTarget {
    open val controller: ServiceWorker
    open var oncontrollerchange: ((Event) -> dynamic)?
    open var onmessage: ((MessageEvent) -> dynamic)?
    open val ready: Promise
    open fun getRegistration(clientURL: String): Promise
    open fun getRegistrations(): Promise
    open fun register(scriptURL: String, options: RegistrationOptions): Promise
    open fun startMessages()
}

public external abstract class ServiceWorkerGlobalScope : WorkerGlobalScope {
    open val clients: Clients
    open var onactivate: ((Event) -> dynamic)?
    open var onfetch: ((FetchEvent) -> dynamic)?
    open var onforeignfetch: ((Event) -> dynamic)?
    open var onfunctionalevent: ((Event) -> dynamic)?
    open var oninstall: ((Event) -> dynamic)?
    open var onmessage: ((MessageEvent) -> dynamic)?
    open var onnotificationclick: ((NotificationEvent) -> dynamic)?
    open var onnotificationclose: ((NotificationEvent) -> dynamic)?
    open val registration: ServiceWorkerRegistration
    open fun skipWaiting(): Promise
}

public external open class ServiceWorkerMessageEvent : Event {
    open val data: Any
    open val lastEventId: String
    open val origin: String
    open val ports: Array
    open val source: UnionMessagePortOrServiceWorker
}

public external interface ServiceWorkerMessageEventInit : EventInit

public external abstract class ServiceWorkerRegistration : EventTarget {
    open val APISpace: dynamic
    open val active: ServiceWorker
    open val installing: ServiceWorker
    open var onupdatefound: ((Event) -> dynamic)?
    open val scope: String
    open val waiting: ServiceWorker
    open fun getNotifications(filter: GetNotificationOptions): Promise
    open fun methodName(): Promise
    open fun showNotification(title: String, options: NotificationOptions): Promise
    open fun unregister(): Promise
    open fun update(): Promise
}

public external interface ServiceWorkerState

public external interface UnionClientOrMessagePortOrServiceWorker

public external interface UnionMessagePortOrServiceWorker

public external abstract class WindowClient : Client {
    open val focused: Boolean
    open val visibilityState: dynamic
    open fun focus(): Promise
    open fun navigate(url: String): Promise
}
// --------- org.w3c.xhr ---------
package org.w3c.xhr


public external open class FormData {
    open fun append(name: String, value: String)
    open fun append(name: String, value: Blob, filename: String)
    open fun delete(name: String)
    open fun get(name: String): dynamic
    open fun getAll(name: String): Array
    open fun has(name: String): Boolean
    open fun set(name: String, value: String)
    open fun set(name: String, value: Blob, filename: String)
}

public external open class ProgressEvent : Event {
    open val lengthComputable: Boolean
    open val loaded: Number
    open val total: Number
}

public external interface ProgressEventInit : EventInit

public external open class XMLHttpRequest : XMLHttpRequestEventTarget {
    open var onreadystatechange: ((Event) -> dynamic)?
    open val readyState: Short
    open val response: Any
    open val responseText: String
    open var responseType: XMLHttpRequestResponseType
    open val responseURL: String
    open val responseXML: Document
    open val status: Short
    open val statusText: String
    open var timeout: Int
    open val upload: XMLHttpRequestUpload
    open var withCredentials: Boolean
    open fun abort()
    open fun getAllResponseHeaders(): String
    open fun getResponseHeader(name: String): String
    open fun open(method: String, url: String)
    open fun open(method: String, url: String, async: Boolean, username: String, password: String)
    open fun overrideMimeType(mime: String)
    open fun send(body: dynamic)
    open fun setRequestHeader(name: String, value: String)
}

public external abstract class XMLHttpRequestEventTarget : EventTarget {
    open var onabort: ((Event) -> dynamic)?
    open var onerror: ((Event) -> dynamic)?
    open var onload: ((Event) -> dynamic)?
    open var onloadend: ((Event) -> dynamic)?
    open var onloadstart: ((ProgressEvent) -> dynamic)?
    open var onprogress: ((ProgressEvent) -> dynamic)?
    open var ontimeout: ((Event) -> dynamic)?
}

public external interface XMLHttpRequestResponseType

public external abstract class XMLHttpRequestUpload : XMLHttpRequestEventTarget