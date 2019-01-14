interface Foo {
    varWithoutTypeAnnotation;
    varAsAny: any;
    varAsNumber: number;
    varAsBoolean: boolean;
    varAsString: string;
}
declare namespace foo {
    interface Bar {
        name: string;
    }
}
