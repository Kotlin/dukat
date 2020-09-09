
declare module "api" {
  interface NativePlatform {
    compile(): boolean;
  }

  export interface Platform<T> extends NativePlatform {
    pong(): boolean;
  }
}