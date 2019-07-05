declare class ExpectedOverrides {
    equals(a: any);
    hashCode(): number;
    toString(): String;
}

declare class ExpectedOverrides2 {
    equals(a);
}

declare class ExpectedNoOverrides {
    equals();
    equals(a: number);
    equals(a: string);
    hashCode(a: String): number;
    toString(a: number = 1);
}
