declare interface FooIntersectionPart {}

type FooPartAndLiteral = {foo: string; bar;} & FooIntersectionPart

declare class FooIntersection {
    bar(p: FooPartAndLiteral);
}
