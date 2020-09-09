declare class Subscriber<T> {
    _next(value: T)
}

declare class InnerSubscriber<T, R> extends Subscriber<R> {
    outerValue: T
    _next(value: R)
}