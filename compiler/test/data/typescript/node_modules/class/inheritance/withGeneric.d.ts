declare class JQueryXHR extends JQueryPromise<any> implements MyXMLHttpRequest {
    overrideMimeType(mimeType: string): any;
    abort(statusText?: string): void;
}

declare class JQueryPromise<T> {

}
declare interface MyXMLHttpRequest {

}