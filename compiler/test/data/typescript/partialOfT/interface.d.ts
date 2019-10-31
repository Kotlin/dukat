declare interface StaticContext extends Low {}

declare interface Low {
    propInLow: String;
    methodInLow(): boolean;
    lambdaInLow(): boolean;
    producePartialSome(): Partial<Some>;
    create: () => Partial<StaticContext>;
    createFromUnkown: () => Partial<UnkownContext>;
}

declare interface Some extends Low {
    propInSome: string;
}

declare function usePartial(some: Some, partial_some: Partial<Some>);