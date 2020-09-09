/// <reference path="_referencedAliases.d.ts" />

declare var fooKey: Key;

declare function barKey(a: Key|number);
declare function barArray(a: Key[]);

interface Parent {
    (...children: Key[]): Foo;
}
