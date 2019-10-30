export interface Low {
    propInLow: String;
    methodInLow(): boolean;
    lambdaInLow(): boolean;
    producePartialSome(): Partial<Some>;
}

export interface Some extends Low {
    propInSome: string;
}

export function usePartial(some: Some, partial_some: Partial<Some>);