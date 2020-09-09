type SomeProp = "boolean" | "number" | "string";

interface Foo {
    propAliased?: SomeProp;
    propAliasedExtendedL?: SomeProp| "undefined" | "unknown";
    propAliasedExtendedM?: "undefined" | SomeProp | "unknown";
    propAliasedExtendedR?: "undefined" | "unknown" | SomeProp;
}