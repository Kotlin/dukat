type SomeProp = "boolean" | "number" | "string";

interface A{}
interface B{}
interface C{}

interface Foo {
    propAny?: any;
    propNumber?: number;
    propBoolean?: boolean;
    propString?: string;
    propAliased?: SomeProp;
    propSingleLiteral?: "a";
    propABCIntersection?: A & B & C;
    propABC?: A | B | C;
}
declare namespace foo {
    interface Bar {
        name?: string;
    }
}
