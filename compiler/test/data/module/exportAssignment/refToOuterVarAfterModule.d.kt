package refToOuterVarAfterModule

external interface JQueryStatic {
    fun ajax(settings: JQueryAjaxSettings): JQueryXHR
    fun ajax(url: String, settings: JQueryAjaxSettings? = definedExternally /* null */): JQueryXHR
}
@JsModule("jquery")
external val `$`: JQueryStatic = definedExternally
