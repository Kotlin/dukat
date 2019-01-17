declare namespace lib1 {
    interface I {
        x: String
    }
}

declare namespace lib2 {
    function foo(): q.I

    import q = lib1
}