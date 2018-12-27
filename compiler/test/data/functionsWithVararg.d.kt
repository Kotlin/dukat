package functionsWithVararg

external fun withVarargWithoutTypeAnnotation(vararg a: Any): Unit = definedExternally
external fun withVarargAny(vararg a: Any): Any = definedExternally
external fun withVarargNumber(vararg s: Number): String = definedExternally
external fun withManyArguments(n: Number, vararg s: String): Boolean = definedExternally
external fun withVarargWithGenericArrayOfNumber(vararg numbers: Number): String = definedExternally
external fun withVarargWithGenericArrayOfFoo(vararg foos: Foo): String = definedExternally
