type SomeProp = "boolean" | "number" | "string";

interface Foo {
    propAny?: any;
    propNumber?: number;
    propBoolean?: boolean;
    propString?: string;
    propAliased?: SomeProp;
    propSingleLiteral?: "a";
}
declare namespace foo {
    interface Bar {
        name?: string;
    }
}
