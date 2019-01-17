declare namespace lib1 {
    interface I {
        x: String
    }

    namespace lib3 {
        interface K {
        }
    }

    import e = lib3

    namespace lib2 {
        import q = lib1

        interface J {
            y: q.I
        }

        namespace lib3 {
            import w = lib2

            function foo(x: w.J, y: q.I, z: q.lib3.K, v: e.K): void
        }
    }
}