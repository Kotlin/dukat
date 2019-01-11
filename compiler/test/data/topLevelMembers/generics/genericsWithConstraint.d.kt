external fun <T : Foo> extendsFooT(a: T): T = definedExternally
external fun <T : Any> extendsAny(a: T): T = definedExternally
external fun <A : Bar, B : A> withManyExtends(a: A, b: B): Boolean = definedExternally
