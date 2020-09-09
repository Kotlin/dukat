export interface Observer<T> {
    next: (value: T) => void;
}

export declare class Subscriber<T> implements Observer<T> {
    next(value?: T): void;
}