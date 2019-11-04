declare class ExpectedOverrides {
    equals(a: any | undefined);
    hashCode(): number;
    toString(): String;
}

declare class ExpectedOverrides2 {
    equals(a: any | null);
}

declare class ExpectedNoOverrides {
    equals();
    equals(a: any);
    equals(a: number);
    equals(a: string);
    hashCode(a: String): number;
    toString(a: number = 1);
}
