type Options = Partial<{
    min: number,
    max: number,
}>;

declare class App {
    constructor(opts?: Options);
    pick<T, K extends keyof T>(obj: T, keys: K[]): Pick<T, K>;
}

export = App;
