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