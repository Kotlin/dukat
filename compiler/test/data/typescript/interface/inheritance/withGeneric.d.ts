interface JQueryXHR extends MyXMLHttpRequest, JQueryPromise<any> {
    overrideMimeType(mimeType: string): any;
    abort(statusText?: string): void;
}

interface Property<T>{}
interface PropertySpec extends Property<"ping" | "pong">  {}
interface MyXMLHttpRequest {

}
interface JQueryPromise<T> {

}