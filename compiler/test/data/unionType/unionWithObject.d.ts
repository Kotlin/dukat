//based on https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/algebra.js/index.d.ts

declare namespace algebra.js {
    function toTex(input: Fraction | Expression | Equation | object | Array<Fraction | object>): string;
    class Equation {}
    class Expression {}
    class Fraction {}
}
export = algebra.js;
