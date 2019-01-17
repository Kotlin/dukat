declare namespace b {
    namespace c {
        function foo(): string
    }
}

declare module "a" {
    export = b.c
}