declare module "_api" {
  export class Hook<A = any, B = any, C = any, D = any, E = any> {}
  export class SyncHook<T1 = any, T2 = string, T3 = any> extends Hook<T1, T2, T3, any, undefined> {}
}