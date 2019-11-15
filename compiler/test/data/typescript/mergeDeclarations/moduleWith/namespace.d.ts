declare module "child_process" {

  interface PromiseWithChild<T> extends Promise<T> {}

  namespace exec {
    function __promisify__(command: string): PromiseWithChild<{ stdout: string, stderr: string }>;
  }

}
