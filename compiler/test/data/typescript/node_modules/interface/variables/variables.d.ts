interface Foo {
    varWithoutTypeAnnotation;
    varAsAny: any;
    varAsNumber: number;
    varAsBoolean: boolean;
    varAsString: string;
    varAsSimpleLambda: (force: boolean) => void;
}
declare namespace foo {
    interface Bar {
        name: string;
    }
}
