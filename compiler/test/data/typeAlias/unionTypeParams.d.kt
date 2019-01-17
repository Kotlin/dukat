package unionTypeParams

external interface Map<K, V>
external interface List<T>
external var aliasUnionVar: dynamic /* List<Number> | Map<String, List<Number>> */ = definedExternally
external fun aliasUnionFunction(a: List<String>): Unit = definedExternally
external fun aliasUnionFunction(a: Map<Number, List<String>>): Unit = definedExternally
external var listOfUnionVar: List<dynamic /* String | Number */> = definedExternally
external fun listOfUnionFunction(a: List<dynamic /* Number | String */>): Unit = definedExternally
