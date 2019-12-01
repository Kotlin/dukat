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

interface WeaklikeMaplike<K extends object, V> {
    delete(key: K): boolean;
    get(key: K): V | undefined;
    has(key: K): boolean;
    set(key: K, value: V): this;
}