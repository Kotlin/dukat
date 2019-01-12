declare class JQueryXHR extends JQueryPromise<any> implements MyXMLHttpRequest {
    overrideMimeType(mimeType: string): any;
    abort(statusText?: string): void;
}
