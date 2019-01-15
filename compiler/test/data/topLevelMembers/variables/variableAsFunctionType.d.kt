package variableAsFunctionType

external var withoutArguments: () -> Unit = definedExternally
external var withOneArgument: (withOutType: Any) -> String = definedExternally
external var withManyArguments: (num: Number, s: String, a: Any) -> Boolean = definedExternally
external var withOptionalArguments: (num: Number? /*= null*/, s: String? /*= null*/) -> Boolean = definedExternally
