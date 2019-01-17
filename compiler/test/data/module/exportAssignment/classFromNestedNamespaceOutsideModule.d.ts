declare namespace b {
    namespace c {
        class D {
        }
    }
}

declare module "a" {
    export = b.c.D
}