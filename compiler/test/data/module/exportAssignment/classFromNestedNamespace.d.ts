declare module "a" {
    namespace b {
        namespace c {
            class D {
            }
        }
    }

    export = b.c.D
}