package withGeneric

external interface JQueryXHR : MyXMLHttpRequest, JQueryPromise<Any> {
    fun overrideMimeType(mimeType: String): Any
    fun abort(statusText: String? = definedExternally /* null */)
}
