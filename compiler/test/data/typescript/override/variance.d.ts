export declare class Observable<T> {
    source: Observable<any>
}

export declare class ConnectableObservable<T> extends Observable<T> {
    source: Observable<T>
}