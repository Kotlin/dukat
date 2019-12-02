
declare namespace api {

  interface Watching {
    close(callback: () => void): void;
    invalidate(): void;
  }

  namespace Tooling {

    class Watching implements Watching {

      close(callback: () => void): void;

      invalidate(): void;
    }

    namespace Watching {
      type WatchOptions = any;
      type Handler = any;
    }
  }
}

export = api;




