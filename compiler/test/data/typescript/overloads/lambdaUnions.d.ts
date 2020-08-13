type HookMethod<O, R> = (options: O) => R | Promise<R>

type BeforeHook<O> = (options: O) => void
type ErrorHook<O, E> = (error: E, options: O) => void
type AfterHook<O, R> = (result: R, options: O) => void
type WrapHook<O, R> = (
    hookMethod: HookMethod<O, R>,
    options: O
) => R | Promise<R>

type AnyHook<O, R, E> =
    | BeforeHook<O>
    | ErrorHook<O, E>
    | AfterHook<O, R>
    | WrapHook<O, R>

export interface HookCollection {
    remove(name: string, hook: AnyHook<any, any, any>): void
}