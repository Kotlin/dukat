package org.jetbrains.dukat.stdlib

import org.jetbrains.dukat.astCommon.*

val KotlinStdlibEntities = setOf(
        IdentifierEntity("Any"),
        IdentifierEntity("Array"),
        IdentifierEntity("Boolean"),
        IdentifierEntity("Error"),
        IdentifierEntity("Function"),
        IdentifierEntity("Number"),
        IdentifierEntity("RegExp"),
        IdentifierEntity("ReadonlyArray"),
        IdentifierEntity("String"),
        IdentifierEntity("Unit"),

        IdentifierEntity("dynamic"),

        IdentifierEntity("AbstractWorker"),
        IdentifierEntity("AddEventListenerOptions"),
        IdentifierEntity("ApplicationCache"),
        IdentifierEntity("ArrayBuffer"),
        IdentifierEntity("ArrayBufferView"),
        IdentifierEntity("AssignedNodesOptions"),
        IdentifierEntity("Attr"),
        IdentifierEntity("Audio"),
        IdentifierEntity("AudioTrack"),
        IdentifierEntity("AudioTrackList"),
        IdentifierEntity("BarProp"),
        IdentifierEntity("BeforeUnloadEvent"),
        IdentifierEntity("BinaryType"),
        IdentifierEntity("Blob"),
        IdentifierEntity("BlobPropertyBag"),
        IdentifierEntity("Body"),
        IdentifierEntity("BoxQuadOptions"),
        IdentifierEntity("BroadcastChannel"),
        IdentifierEntity("BufferDataSource"),
        IdentifierEntity("CDATASection"),
        IdentifierEntity("CSSBoxType"),
        IdentifierEntity("Cache"),
        IdentifierEntity("CacheBatchOperation"),
        IdentifierEntity("CacheQueryOptions"),
        IdentifierEntity("CacheStorage"),
        IdentifierEntity("CanPlayTypeResult"),
        IdentifierEntity("CanvasCompositing"),
        IdentifierEntity("CanvasDirection"),
        IdentifierEntity("CanvasDrawImage"),
        IdentifierEntity("CanvasDrawPath"),
        IdentifierEntity("CanvasFillRule"),
        IdentifierEntity("CanvasFillStrokeStyles"),
        IdentifierEntity("CanvasFilters"),
        IdentifierEntity("CanvasGradient"),
        IdentifierEntity("CanvasHitRegion"),
        IdentifierEntity("CanvasImageData"),
        IdentifierEntity("CanvasImageSmoothing"),
        IdentifierEntity("CanvasImageSource"),
        IdentifierEntity("CanvasLineCap"),
        IdentifierEntity("CanvasLineJoin"),
        IdentifierEntity("CanvasPath"),
        IdentifierEntity("CanvasPathDrawingStyles"),
        IdentifierEntity("CanvasPattern"),
        IdentifierEntity("CanvasRect"),
        IdentifierEntity("CanvasRenderingContext2D"),
        IdentifierEntity("CanvasRenderingContext2DSettings"),
        IdentifierEntity("CanvasShadowStyles"),
        IdentifierEntity("CanvasState"),
        IdentifierEntity("CanvasText"),
        IdentifierEntity("CanvasTextAlign"),
        IdentifierEntity("CanvasTextBaseline"),
        IdentifierEntity("CanvasTextDrawingStyles"),
        IdentifierEntity("CanvasTransform"),
        IdentifierEntity("CanvasUserInterface"),
        IdentifierEntity("CaretPosition"),
        IdentifierEntity("CharacterData"),
        IdentifierEntity("ChildNode"),
        IdentifierEntity("Client"),
        IdentifierEntity("ClientQueryOptions"),
        IdentifierEntity("ClientType"),
        IdentifierEntity("Clients"),
        IdentifierEntity("CloseEvent"),
        IdentifierEntity("CloseEventInit"),
        IdentifierEntity("ColorSpaceConversion"),
        IdentifierEntity("Comment"),
        IdentifierEntity("CompositionEvent"),
        IdentifierEntity("CompositionEventInit"),
        IdentifierEntity("Console"),
        IdentifierEntity("ConvertCoordinateOptions"),
        IdentifierEntity("CustomElementRegistry"),
        IdentifierEntity("CustomEvent"),
        IdentifierEntity("CustomEventInit"),
        IdentifierEntity("DOMImplementation"),
        IdentifierEntity("DOMMatrix"),
        IdentifierEntity("DOMMatrixReadOnly"),
        IdentifierEntity("DOMParser"),
        IdentifierEntity("DOMPoint"),
        IdentifierEntity("DOMPointInit"),
        IdentifierEntity("DOMPointReadOnly"),
        IdentifierEntity("DOMQuad"),
        IdentifierEntity("DOMRect"),
        IdentifierEntity("DOMRectInit"),
        IdentifierEntity("DOMRectList"),
        IdentifierEntity("DOMRectReadOnly"),
        IdentifierEntity("DOMStringMap"),
        IdentifierEntity("DOMTokenList"),
        IdentifierEntity("DataTransfer"),
        IdentifierEntity("DataTransferItem"),
        IdentifierEntity("DataTransferItemList"),
        IdentifierEntity("DataView"),
        IdentifierEntity("Date"),
        IdentifierEntity("DedicatedWorkerGlobalScope"),
        IdentifierEntity("Document"),
        IdentifierEntity("DocumentAndElementEventHandlers"),
        IdentifierEntity("DocumentFragment"),
        IdentifierEntity("DocumentOrShadowRoot"),
        IdentifierEntity("DocumentReadyState"),
        IdentifierEntity("DocumentType"),
        IdentifierEntity("DragEvent"),
        IdentifierEntity("DragEventInit"),
        IdentifierEntity("Element"),
        IdentifierEntity("ElementContentEditable"),
        IdentifierEntity("ElementCreationOptions"),
        IdentifierEntity("ElementDefinitionOptions"),
        IdentifierEntity("ErrorEvent"),
        IdentifierEntity("ErrorEventInit"),
        IdentifierEntity("Event"),
        IdentifierEntity("EventInit"),
        IdentifierEntity("EventListener"),
        IdentifierEntity("EventListenerHandler"),
        IdentifierEntity("EventListenerOptions"),
        IdentifierEntity("EventModifierInit"),
        IdentifierEntity("EventSource"),
        IdentifierEntity("EventSourceInit"),
        IdentifierEntity("EventTarget"),
        IdentifierEntity("ExtendableEvent"),
        IdentifierEntity("ExtendableEventInit"),
        IdentifierEntity("ExtendableMessageEvent"),
        IdentifierEntity("ExtendableMessageEventInit"),
        IdentifierEntity("External"),
        IdentifierEntity("FetchEvent"),
        IdentifierEntity("FetchEventInit"),
        IdentifierEntity("File"),
        IdentifierEntity("FileList"),
        IdentifierEntity("FilePropertyBag"),
        IdentifierEntity("FileReader"),
        IdentifierEntity("FileReaderSync"),
        IdentifierEntity("Float32Array"),
        IdentifierEntity("Float64Array"),
        IdentifierEntity("FocusEvent"),
        IdentifierEntity("FocusEventInit"),
        IdentifierEntity("ForeignFetchEvent"),
        IdentifierEntity("ForeignFetchEventInit"),
        IdentifierEntity("ForeignFetchOptions"),
        IdentifierEntity("ForeignFetchResponse"),
        IdentifierEntity("FormData"),
        IdentifierEntity("FrameType"),
        IdentifierEntity("FunctionalEvent"),
        IdentifierEntity("GeometryUtils"),
        IdentifierEntity("GetNotificationOptions"),
        IdentifierEntity("GetRootNodeOptions"),
        IdentifierEntity("GetSVGDocument"),
        IdentifierEntity("GlobalEventHandlers"),
        IdentifierEntity("GlobalPerformance"),
        IdentifierEntity("HTMLAllCollection"),
        IdentifierEntity("HTMLAnchorElement"),
        IdentifierEntity("HTMLAppletElement"),
        IdentifierEntity("HTMLAreaElement"),
        IdentifierEntity("HTMLAudioElement"),
        IdentifierEntity("HTMLBRElement"),
        IdentifierEntity("HTMLBaseElement"),
        IdentifierEntity("HTMLBodyElement"),
        IdentifierEntity("HTMLButtonElement"),
        IdentifierEntity("HTMLCanvasElement"),
        IdentifierEntity("HTMLCollection"),
        IdentifierEntity("HTMLDListElement"),
        IdentifierEntity("HTMLDataElement"),
        IdentifierEntity("HTMLDataListElement"),
        IdentifierEntity("HTMLDetailsElement"),
        IdentifierEntity("HTMLDialogElement"),
        IdentifierEntity("HTMLDirectoryElement"),
        IdentifierEntity("HTMLDivElement"),
        IdentifierEntity("HTMLElement"),
        IdentifierEntity("HTMLEmbedElement"),
        IdentifierEntity("HTMLFieldSetElement"),
        IdentifierEntity("HTMLFontElement"),
        IdentifierEntity("HTMLFormControlsCollection"),
        IdentifierEntity("HTMLFormElement"),
        IdentifierEntity("HTMLFrameElement"),
        IdentifierEntity("HTMLFrameSetElement"),
        IdentifierEntity("HTMLHRElement"),
        IdentifierEntity("HTMLHeadElement"),
        IdentifierEntity("HTMLHeadingElement"),
        IdentifierEntity("HTMLHtmlElement"),
        IdentifierEntity("HTMLHyperlinkElementUtils"),
        IdentifierEntity("HTMLIFrameElement"),
        IdentifierEntity("HTMLImageElement"),
        IdentifierEntity("HTMLInputElement"),
        IdentifierEntity("HTMLKeygenElement"),
        IdentifierEntity("HTMLLIElement"),
        IdentifierEntity("HTMLLabelElement"),
        IdentifierEntity("HTMLLegendElement"),
        IdentifierEntity("HTMLLinkElement"),
        IdentifierEntity("HTMLMapElement"),
        IdentifierEntity("HTMLMarqueeElement"),
        IdentifierEntity("HTMLMediaElement"),
        IdentifierEntity("HTMLMenuElement"),
        IdentifierEntity("HTMLMenuItemElement"),
        IdentifierEntity("HTMLMetaElement"),
        IdentifierEntity("HTMLMeterElement"),
        IdentifierEntity("HTMLModElement"),
        IdentifierEntity("HTMLOListElement"),
        IdentifierEntity("HTMLObjectElement"),
        IdentifierEntity("HTMLOptGroupElement"),
        IdentifierEntity("HTMLOptionElement"),
        IdentifierEntity("HTMLOptionsCollection"),
        IdentifierEntity("HTMLOrSVGImageElement"),
        IdentifierEntity("HTMLOrSVGScriptElement"),
        IdentifierEntity("HTMLOutputElement"),
        IdentifierEntity("HTMLParagraphElement"),
        IdentifierEntity("HTMLParamElement"),
        IdentifierEntity("HTMLPictureElement"),
        IdentifierEntity("HTMLPreElement"),
        IdentifierEntity("HTMLProgressElement"),
        IdentifierEntity("HTMLQuoteElement"),
        IdentifierEntity("HTMLScriptElement"),
        IdentifierEntity("HTMLSelectElement"),
        IdentifierEntity("HTMLSlotElement"),
        IdentifierEntity("HTMLSourceElement"),
        IdentifierEntity("HTMLSpanElement"),
        IdentifierEntity("HTMLStyleElement"),
        IdentifierEntity("HTMLTableCaptionElement"),
        IdentifierEntity("HTMLTableCellElement"),
        IdentifierEntity("HTMLTableColElement"),
        IdentifierEntity("HTMLTableElement"),
        IdentifierEntity("HTMLTableRowElement"),
        IdentifierEntity("HTMLTableSectionElement"),
        IdentifierEntity("HTMLTemplateElement"),
        IdentifierEntity("HTMLTextAreaElement"),
        IdentifierEntity("HTMLTimeElement"),
        IdentifierEntity("HTMLTitleElement"),
        IdentifierEntity("HTMLTrackElement"),
        IdentifierEntity("HTMLUListElement"),
        IdentifierEntity("HTMLUnknownElement"),
        IdentifierEntity("HTMLVideoElement"),
        IdentifierEntity("HashChangeEvent"),
        IdentifierEntity("HashChangeEventInit"),
        IdentifierEntity("Headers"),
        IdentifierEntity("History"),
        IdentifierEntity("HitRegionOptions"),
        IdentifierEntity("Image"),
        IdentifierEntity("ImageBitmap"),
        IdentifierEntity("ImageBitmapOptions"),
        IdentifierEntity("ImageBitmapRenderingContext"),
        IdentifierEntity("ImageBitmapRenderingContextSettings"),
        IdentifierEntity("ImageBitmapSource"),
        IdentifierEntity("ImageData"),
        IdentifierEntity("ImageOrientation"),
        IdentifierEntity("ImageSmoothingQuality"),
        IdentifierEntity("InputEvent"),
        IdentifierEntity("InputEventInit"),
        IdentifierEntity("InstallEvent"),
        IdentifierEntity("Int16Array"),
        IdentifierEntity("Int32Array"),
        IdentifierEntity("Int8Array"),
        IdentifierEntity("ItemArrayLike"),
        IdentifierEntity("JsClass"),
        IdentifierEntity("Json"),
        IdentifierEntity("KeyboardEvent"),
        IdentifierEntity("KeyboardEventInit"),
        IdentifierEntity("Location"),
        IdentifierEntity("MediaError"),
        IdentifierEntity("MediaQueryList"),
        IdentifierEntity("MediaQueryListEvent"),
        IdentifierEntity("MediaQueryListEventInit"),
        IdentifierEntity("MessageChannel"),
        IdentifierEntity("MessageEvent"),
        IdentifierEntity("MessageEventInit"),
        IdentifierEntity("MessagePort"),
        IdentifierEntity("MimeType"),
        IdentifierEntity("MimeTypeArray"),
        IdentifierEntity("MouseEvent"),
        IdentifierEntity("MouseEventInit"),
        IdentifierEntity("MutationObserver"),
        IdentifierEntity("MutationObserverInit"),
        IdentifierEntity("MutationRecord"),
        IdentifierEntity("NamedNodeMap"),
        IdentifierEntity("Navigator"),
        IdentifierEntity("NavigatorConcurrentHardware"),
        IdentifierEntity("NavigatorContentUtils"),
        IdentifierEntity("NavigatorCookies"),
        IdentifierEntity("NavigatorID"),
        IdentifierEntity("NavigatorLanguage"),
        IdentifierEntity("NavigatorOnLine"),
        IdentifierEntity("NavigatorPlugins"),
        IdentifierEntity("Node"),
        IdentifierEntity("NodeFilter"),
        IdentifierEntity("NodeIterator"),
        IdentifierEntity("NodeList"),
        IdentifierEntity("NonDocumentTypeChildNode"),
        IdentifierEntity("NonElementParentNode"),
        IdentifierEntity("Notification"),
        IdentifierEntity("NotificationAction"),
        IdentifierEntity("NotificationDirection"),
        IdentifierEntity("NotificationEvent"),
        IdentifierEntity("NotificationEventInit"),
        IdentifierEntity("NotificationOptions"),
        IdentifierEntity("NotificationPermission"),
        IdentifierEntity("Option"),
        IdentifierEntity("PageTransitionEvent"),
        IdentifierEntity("PageTransitionEventInit"),
        IdentifierEntity("ParentNode"),
        IdentifierEntity("Path2D"),
        IdentifierEntity("Performance"),
        IdentifierEntity("PerformanceNavigation"),
        IdentifierEntity("PerformanceTiming"),
        IdentifierEntity("Plugin"),
        IdentifierEntity("PluginArray"),
        IdentifierEntity("PopStateEvent"),
        IdentifierEntity("PopStateEventInit"),
        IdentifierEntity("PremultiplyAlpha"),
        IdentifierEntity("ProcessingInstruction"),
        IdentifierEntity("ProgressEvent"),
        IdentifierEntity("ProgressEventInit"),
        IdentifierEntity("Promise"),
        IdentifierEntity("PromiseRejectionEvent"),
        IdentifierEntity("PromiseRejectionEventInit"),
        IdentifierEntity("RadioNodeList"),
        IdentifierEntity("Range"),
        IdentifierEntity("RegExp"),
        IdentifierEntity("RegExpMatch"),
        IdentifierEntity("RegistrationOptions"),
        IdentifierEntity("RelatedEvent"),
        IdentifierEntity("RelatedEventInit"),
        IdentifierEntity("RenderingContext"),
        IdentifierEntity("Request"),
        IdentifierEntity("RequestCache"),
        IdentifierEntity("RequestCredentials"),
        IdentifierEntity("RequestDestination"),
        IdentifierEntity("RequestInit"),
        IdentifierEntity("RequestMode"),
        IdentifierEntity("RequestRedirect"),
        IdentifierEntity("RequestType"),
        IdentifierEntity("ResizeQuality"),
        IdentifierEntity("Response"),
        IdentifierEntity("ResponseInit"),
        IdentifierEntity("ResponseType"),
        IdentifierEntity("SVGAElement"),
        IdentifierEntity("SVGAngle"),
        IdentifierEntity("SVGAnimatedAngle"),
        IdentifierEntity("SVGAnimatedBoolean"),
        IdentifierEntity("SVGAnimatedEnumeration"),
        IdentifierEntity("SVGAnimatedInteger"),
        IdentifierEntity("SVGAnimatedLength"),
        IdentifierEntity("SVGAnimatedLengthList"),
        IdentifierEntity("SVGAnimatedNumber"),
        IdentifierEntity("SVGAnimatedNumberList"),
        IdentifierEntity("SVGAnimatedPoints"),
        IdentifierEntity("SVGAnimatedPreserveAspectRatio"),
        IdentifierEntity("SVGAnimatedRect"),
        IdentifierEntity("SVGAnimatedString"),
        IdentifierEntity("SVGAnimatedTransformList"),
        IdentifierEntity("SVGBoundingBoxOptions"),
        IdentifierEntity("SVGCircleElement"),
        IdentifierEntity("SVGCursorElement"),
        IdentifierEntity("SVGDefsElement"),
        IdentifierEntity("SVGDescElement"),
        IdentifierEntity("SVGElement"),
        IdentifierEntity("SVGElementInstance"),
        IdentifierEntity("SVGEllipseElement"),
        IdentifierEntity("SVGFitToViewBox"),
        IdentifierEntity("SVGForeignObjectElement"),
        IdentifierEntity("SVGGElement"),
        IdentifierEntity("SVGGeometryElement"),
        IdentifierEntity("SVGGradientElement"),
        IdentifierEntity("SVGGraphicsElement"),
        IdentifierEntity("SVGHatchElement"),
        IdentifierEntity("SVGHatchpathElement"),
        IdentifierEntity("SVGImageElement"),
        IdentifierEntity("SVGLength"),
        IdentifierEntity("SVGLengthList"),
        IdentifierEntity("SVGLineElement"),
        IdentifierEntity("SVGLinearGradientElement"),
        IdentifierEntity("SVGMarkerElement"),
        IdentifierEntity("SVGMeshElement"),
        IdentifierEntity("SVGMeshGradientElement"),
        IdentifierEntity("SVGMeshpatchElement"),
        IdentifierEntity("SVGMeshrowElement"),
        IdentifierEntity("SVGMetadataElement"),
        IdentifierEntity("SVGNameList"),
        IdentifierEntity("SVGNumber"),
        IdentifierEntity("SVGNumberList"),
        IdentifierEntity("SVGPathElement"),
        IdentifierEntity("SVGPatternElement"),
        IdentifierEntity("SVGPointList"),
        IdentifierEntity("SVGPolygonElement"),
        IdentifierEntity("SVGPolylineElement"),
        IdentifierEntity("SVGPreserveAspectRatio"),
        IdentifierEntity("SVGRadialGradientElement"),
        IdentifierEntity("SVGRectElement"),
        IdentifierEntity("SVGSVGElement"),
        IdentifierEntity("SVGScriptElement"),
        IdentifierEntity("SVGSolidcolorElement"),
        IdentifierEntity("SVGStopElement"),
        IdentifierEntity("SVGStringList"),
        IdentifierEntity("SVGStyleElement"),
        IdentifierEntity("SVGSwitchElement"),
        IdentifierEntity("SVGSymbolElement"),
        IdentifierEntity("SVGTSpanElement"),
        IdentifierEntity("SVGTests"),
        IdentifierEntity("SVGTextContentElement"),
        IdentifierEntity("SVGTextElement"),
        IdentifierEntity("SVGTextPathElement"),
        IdentifierEntity("SVGTextPositioningElement"),
        IdentifierEntity("SVGTitleElement"),
        IdentifierEntity("SVGTransform"),
        IdentifierEntity("SVGTransformList"),
        IdentifierEntity("SVGURIReference"),
        IdentifierEntity("SVGUnitTypes"),
        IdentifierEntity("SVGUnknownElement"),
        IdentifierEntity("SVGUseElement"),
        IdentifierEntity("SVGUseElementShadowRoot"),
        IdentifierEntity("SVGViewElement"),
        IdentifierEntity("SVGZoomAndPan"),
        IdentifierEntity("Screen"),
        IdentifierEntity("ScrollBehavior"),
        IdentifierEntity("ScrollIntoViewOptions"),
        IdentifierEntity("ScrollLogicalPosition"),
        IdentifierEntity("ScrollOptions"),
        IdentifierEntity("ScrollRestoration"),
        IdentifierEntity("ScrollToOptions"),
        IdentifierEntity("SelectionMode"),
        IdentifierEntity("ServiceWorker"),
        IdentifierEntity("ServiceWorkerContainer"),
        IdentifierEntity("ServiceWorkerGlobalScope"),
        IdentifierEntity("ServiceWorkerMessageEvent"),
        IdentifierEntity("ServiceWorkerMessageEventInit"),
        IdentifierEntity("ServiceWorkerRegistration"),
        IdentifierEntity("ServiceWorkerState"),
        IdentifierEntity("ShadowAnimation"),
        IdentifierEntity("ShadowRoot"),
        IdentifierEntity("ShadowRootInit"),
        IdentifierEntity("ShadowRootMode"),
        IdentifierEntity("SharedWorker"),
        IdentifierEntity("SharedWorkerGlobalScope"),
        IdentifierEntity("Slotable"),
        IdentifierEntity("Storage"),
        IdentifierEntity("StorageEvent"),
        IdentifierEntity("StorageEventInit"),
        IdentifierEntity("TexImageSource"),
        IdentifierEntity("Text"),
        IdentifierEntity("TextMetrics"),
        IdentifierEntity("TextTrack"),
        IdentifierEntity("TextTrackCue"),
        IdentifierEntity("TextTrackCueList"),
        IdentifierEntity("TextTrackKind"),
        IdentifierEntity("TextTrackList"),
        IdentifierEntity("TextTrackMode"),
        IdentifierEntity("TimeRanges"),
        IdentifierEntity("Touch"),
        IdentifierEntity("TouchEvent"),
        IdentifierEntity("TouchList"),
        IdentifierEntity("TrackEvent"),
        IdentifierEntity("TrackEventInit"),
        IdentifierEntity("TreeWalker"),
        IdentifierEntity("UIEvent"),
        IdentifierEntity("UIEventInit"),
        IdentifierEntity("URL"),
        IdentifierEntity("URLSearchParams"),
        IdentifierEntity("Uint16Array"),
        IdentifierEntity("Uint32Array"),
        IdentifierEntity("Uint8Array"),
        IdentifierEntity("Uint8ClampedArray"),
        IdentifierEntity("UnionAudioTrackOrTextTrackOrVideoTrack"),
        IdentifierEntity("UnionClientOrMessagePortOrServiceWorker"),
        IdentifierEntity("UnionElementOrHTMLCollection"),
        IdentifierEntity("UnionElementOrMouseEvent"),
        IdentifierEntity("UnionElementOrRadioNodeList"),
        IdentifierEntity("UnionHTMLOptGroupElementOrHTMLOptionElement"),
        IdentifierEntity("UnionMessagePortOrServiceWorker"),
        IdentifierEntity("UnionMessagePortOrWindowProxy"),
        IdentifierEntity("ValidityState"),
        IdentifierEntity("VideoTrack"),
        IdentifierEntity("VideoTrackList"),
        IdentifierEntity("WebGLActiveInfo"),
        IdentifierEntity("WebGLBuffer"),
        IdentifierEntity("WebGLContextAttributes"),
        IdentifierEntity("WebGLContextEvent"),
        IdentifierEntity("WebGLContextEventInit"),
        IdentifierEntity("WebGLFramebuffer"),
        IdentifierEntity("WebGLObject"),
        IdentifierEntity("WebGLProgram"),
        IdentifierEntity("WebGLRenderbuffer"),
        IdentifierEntity("WebGLRenderingContext"),
        IdentifierEntity("WebGLRenderingContextBase"),
        IdentifierEntity("WebGLShader"),
        IdentifierEntity("WebGLShaderPrecisionFormat"),
        IdentifierEntity("WebGLTexture"),
        IdentifierEntity("WebGLUniformLocation"),
        IdentifierEntity("WebSocket"),
        IdentifierEntity("WheelEvent"),
        IdentifierEntity("WheelEventInit"),
        IdentifierEntity("Window"),
        IdentifierEntity("WindowClient"),
        IdentifierEntity("WindowEventHandlers"),
        IdentifierEntity("WindowLocalStorage"),
        IdentifierEntity("WindowOrWorkerGlobalScope"),
        IdentifierEntity("WindowSessionStorage"),
        IdentifierEntity("Worker"),
        IdentifierEntity("WorkerGlobalScope"),
        IdentifierEntity("WorkerLocation"),
        IdentifierEntity("WorkerNavigator"),
        IdentifierEntity("WorkerOptions"),
        IdentifierEntity("WorkerType"),
        IdentifierEntity("XMLDocument"),
        IdentifierEntity("XMLHttpRequest"),
        IdentifierEntity("XMLHttpRequestEventTarget"),
        IdentifierEntity("XMLHttpRequestResponseType"),
        IdentifierEntity("XMLHttpRequestUpload"),
        IdentifierEntity("XMLSerializer")
        )

val KotlinBuiltInEntities = setOf(
    IdentifierEntity("Annotation"),
    IdentifierEntity("Any"),
    IdentifierEntity("Array"),
    IdentifierEntity("Boolean"),
    IdentifierEntity("BooleanArray"),
    IdentifierEntity("Byte"),
    IdentifierEntity("ByteArray"),
    IdentifierEntity("Char"),
    IdentifierEntity("CharSequence"),
    IdentifierEntity("CharSequence"),
    IdentifierEntity("Comparable"),
    IdentifierEntity("Double"),
    IdentifierEntity("DoubleArray"),
    IdentifierEntity("Float"),
    IdentifierEntity("FloatArray"),
    IdentifierEntity("Int"),
    IdentifierEntity("Long"),
    IdentifierEntity("LongArray"),
    IdentifierEntity("Nothing"),
    IdentifierEntity("Number"),
    IdentifierEntity("Short"),
    IdentifierEntity("ShortArray"),
    IdentifierEntity("String"),
    IdentifierEntity("Throwable"),

    //This one is actually is not built in, it's a lie!
    IdentifierEntity("Function")
)

fun isStdLibEntity(fqName: NameEntity): Boolean {
    val isLib = fqName.isTsStdlibPrefixed() || fqName.isKotlinStdlibPrefixed()
    return isLib && KotlinStdlibEntities.contains(fqName.rightMost())
}


fun NameEntity.isKotlinType(fqName: NameEntity): Boolean {
    return equals(fqName) || (fqName.isKotlinStdlibPrefixed() && equals(fqName.rightMost()))
}

fun NameEntity.isAny(): Boolean {
    return isKotlinType(IdentifierEntity("Any"))
}

fun NameEntity.isDynamic(): Boolean {
    return isKotlinType(IdentifierEntity("dynamic"))
}

fun NameEntity.isNumber(): Boolean {
    return isKotlinType(IdentifierEntity("Number"))
}

fun String.asKotlinType(): NameEntity {
    return QualifierEntity(KLIBROOT, IdentifierEntity(this))
}