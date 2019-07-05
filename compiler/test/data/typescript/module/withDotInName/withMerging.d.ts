declare module Foo.Bar.Baz {
    var variableFooBarBaz: number;
    function funcFooBarBaz();
    class AFooBarBaz {}
}

declare module Foo.Bar {
    var variableFooBar: number;
    function funcFooBar();
    class AFooBar {}
}

declare module Foo {
    var variableFoo: number;
    function funcFoo();
    class AFoo {}
}

declare module Foo.Bar.Baz {
    var anotherVariableFooBarBaz: number;
    function anotherFuncFooBarBaz();
    class AnotherAFooBarBaz {}
}
