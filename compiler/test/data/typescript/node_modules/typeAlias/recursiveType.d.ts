interface AnimatedValue {}

type Rec = { foo: Rec };
type Mapping = { [key: string]: Mapping } | AnimatedValue;

declare function foo(): Mapping
declare function bar(d: Mapping)

declare function boo(): Rec
declare function baz(d: Rec)