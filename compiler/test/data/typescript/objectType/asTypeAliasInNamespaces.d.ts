declare namespace a {
    type I = {
        foo(): string
    }

    function foo(): I

    function bar(): b.I
}

declare namespace b {
    type I = {
        foo(): string
    }

    function foo(): I

    function bar(): a.I
}
