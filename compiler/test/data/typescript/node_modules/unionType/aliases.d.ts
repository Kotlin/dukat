declare class Foo {}

type Key = Key2 | number;
type Key2 = string | Foo;
declare var fooKey: Key;

declare function barKey(a: Key|number);
declare function barList(a: List<Key>);
declare function barArray(a: Key[]);

interface Parent {
    (...children: Key[]): Foo;
}

type Arguments<T> = T & {ping(): boolean};

interface Argv<T> {
    command<O>(handler?: (args: Arguments<Array<O>>) => void): Argv<T>;
}