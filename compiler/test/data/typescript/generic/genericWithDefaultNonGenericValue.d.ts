declare interface TriggeredEvent<
  TDelegateTarget = any,
  TData = any,
  TCurrentTarget = any,
  TTarget = any
  >{}

declare interface EventHandlerBase<TContext, T> {}

declare type EventHandler<
  TCurrentTarget,
  TData = undefined
  > = EventHandlerBase<TCurrentTarget, TriggeredEvent<TCurrentTarget, TData>>;

declare namespace yargs {
  interface Argv<T = {}> {
    ping(): T
  }
  interface Arrrrgv<T = {"x": string}> {
    ping(): T
  }
}

declare var yargs: yargs.Argv;
declare var yarrrrgs: yargs.Arrrrgv;