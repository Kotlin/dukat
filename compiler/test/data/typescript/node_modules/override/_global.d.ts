declare namespace GlobalNamespace {

  interface ReadableStream {
    pipe<T extends WritableStream>(destination: T, options?: { end?: boolean; }): T;
    unpipe(destination?: WritableStream): this;
  }

  interface WritableStream {
  }
}