export type APICallback<T = any> = (
    err: GoogleError | null,
    response?: T
) => void;

export type GoogleError = any;

export interface MethodOverload<T, R> {
    (data: T, callback: APICallback<R>): void;
}