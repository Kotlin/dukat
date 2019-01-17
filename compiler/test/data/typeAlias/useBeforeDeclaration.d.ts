interface List<T> {
}

declare var listOfString: Values<string>;

declare function listOfNumberFunction(a: Values<number>);

declare module Foo {
    var listOfString: Values<string>;

    function listOfNumberFunction(a: Values<number>);

    /**TODO should type be List<String> instead of Values<String>?*/
    var myVar: Value;

    function myFunction(a: Value);

    type Value = string
}

type Values<V> = List<V>;
