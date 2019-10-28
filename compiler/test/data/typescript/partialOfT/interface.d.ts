export interface Low {
    propInLow: String;
    methodInLow(): boolean;
    lambdaInLow(): boolean;
}

export interface Some extends Low {
    propInSome: string;
}

export function usePartial(some: Some, partial_some: Partial<Some>);