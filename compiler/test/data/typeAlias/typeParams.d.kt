package typeParams

external interface Map<K, V>
external interface List<T>
external var fooMap: Map<String, List<Number>> = definedExternally
external fun mapKey(a: Map<Number, List<String>>): Unit = definedExternally
external var fooStringOrMap: dynamic /* String | Map<String, List<Number>> */ = definedExternally
external fun stringOrMapKey(a: String): Unit = definedExternally
external fun stringOrMapKey(a: Map<Number, List<String>>): Unit = definedExternally
external var listOfStringOrNumber: dynamic /* String | List<dynamic /* String | Number */> */ = definedExternally
external fun listOfNumberOrString(a: List<dynamic /* Number | String */>): Unit = definedExternally
external var headers: Map<String, List<String>> = definedExternally
external fun getHeaders(): Map<String, List<String>> = definedExternally
external fun addHeaders(headers: Map<String, List<String>>): Unit = definedExternally
external var someRef: dynamic /* String | (instance: T) -> Any */ = definedExternally
external fun addRef(ref: String): Unit = definedExternally
external fun addRef(ref: (instance: T) -> Any): Unit = definedExternally
