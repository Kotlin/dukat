type SomeProp = "boolean" | "number" | "string";

interface Foo {
    propAny?: any;
    propNumber?: number;
    propBoolean?: boolean;
    propString?: string;
    propAliased?: SomeProp;
}
declare namespace foo {
    interface Bar {
        name?: string;
    }
}
