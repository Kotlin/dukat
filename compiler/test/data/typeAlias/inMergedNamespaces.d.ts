declare namespace foo {
    interface J extends A {
    }
}

declare namespace foo {
    interface I {
    }

    type A = I
}