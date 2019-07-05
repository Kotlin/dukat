declare namespace a {
    namespace b {
        class Foo {
        }
    }
}

declare module "c" {
    import x = a.b.Foo
    export = x
}