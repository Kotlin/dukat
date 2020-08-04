declare interface DoneCallback<TResolve = any, TjqXHR = Array<TResolve>> {

}

declare class SomeAPI {
    getCallback(): DoneCallback;
}