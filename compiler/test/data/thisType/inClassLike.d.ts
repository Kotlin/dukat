declare class MyClass {
    self: this;
    that(): this;
    load(ids: string, handler: (self: this, ...args: any[]) => void): void;
}

interface MyInterface {
    self: this;
    that(): this;
    load(ids: string, handler: (self: this, ...args: any[]) => void): void;
}