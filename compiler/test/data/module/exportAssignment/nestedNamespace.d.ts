declare module "a" {
    namespace b {
        namespace c {
            function foo(): string
        }
    }

    export = b.c
}