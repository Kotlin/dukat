package generics

external fun <T> withoutArgumentsReturnsT(): T = definedExternally
external fun <T> withOneT(a: T): Any = definedExternally
external fun <T> returnsT(s: String): T = definedExternally
external fun <A, B> withManyArguments(a: A, b: B): Boolean = definedExternally
external var arrayOfAny: Array<Any> = definedExternally
external var arrayOfArray: Array<Array<String>> = definedExternally
external var arrayOfList: Array<List<String>> = definedExternally
external var arrayOfListBySquare: Array<List<Boolean>> = definedExternally
external var listOfArray: List<Array<Any>> = definedExternally
external var listOfArrayBySquare: List<Array<Number>> = definedExternally
