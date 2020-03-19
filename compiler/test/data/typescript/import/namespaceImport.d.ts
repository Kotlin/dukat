   declare namespace lib1 {
    interface I {
        x: String
    }
}

declare namespace lib2 {
    import q = lib1

    function foo(): q.I
}