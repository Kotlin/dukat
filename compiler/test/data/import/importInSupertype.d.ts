declare namespace lib {
    interface I {
    }
}

declare namespace main {
    import q = lib

    interface J extends q.I {
    }
}