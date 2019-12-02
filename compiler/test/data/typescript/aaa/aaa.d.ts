
declare namespace webpack {

  interface Watching {
    close(callback: () => void): void;
    invalidate(): void;
  }

  namespace Compiler {

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

export = webpack;




