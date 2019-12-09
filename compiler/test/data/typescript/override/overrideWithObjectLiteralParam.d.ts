/// <reference path="_global.d.ts" />

declare module "myStream" {
    class internal {
        pipe<T extends GlobalNamespace.WritableStream>(destination: T, options?: { end?: boolean; }): T;
    }

    namespace internal {
        class Stream extends internal { }

        class Readable extends Stream implements GlobalNamespace.ReadableStream {
            unpipe(destination?: GlobalNamespace.WritableStream): this;
        }
    }

    export = internal;
}
