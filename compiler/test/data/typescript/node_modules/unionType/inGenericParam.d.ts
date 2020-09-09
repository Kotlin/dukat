
export namespace My.Deeply.Nested.Response {
    export interface Response extends Box<string | Box<string>> {
        token: Box<string>;
        expirationToken: Box<string>;
    }

    export interface Box<T> {

    }
}