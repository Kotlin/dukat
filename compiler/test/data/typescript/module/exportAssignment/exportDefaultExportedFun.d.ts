//TODO: this looks something that just can not exist - if we want to test a real-life scenario, declare module should be in a separate nodejs package

declare module "foo1" {
    export function bar(): string

    export function baz()

    export default bar
}