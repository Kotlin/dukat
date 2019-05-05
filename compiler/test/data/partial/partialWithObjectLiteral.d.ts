type Options = Partial<{
    min: number,
    max: number,
}>;

declare class App {
    constructor(opts?: Options);
}

export = App;
