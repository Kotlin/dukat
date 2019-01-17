package refToOuterVarBeforeModule

external interface SinonStatic {
    var createStubInstance: (constructor: Any) -> SinonStub
    var format: (obj: Any) -> String
    var log: (message: String) -> Unit
    fun restore(`object`: Any)
}
@JsModule("sinon")
external val sinon: SinonStatic = definedExternally
