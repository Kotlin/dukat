declare namespace a {
    namespace b {
        function foo(): string
    }
}

declare module "c" {
    import x = a.b
    export = x
}