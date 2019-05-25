declare interface Options {
    mode: ["whatever" | "it" | "will" | "be"]
}

declare var foo: [string, any];

declare interface State {
    set(newValue: {}): [{
        id: number;
        state: {};
    }];
}