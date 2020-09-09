// based on lazy.js.d.ts
declare module LazyJS {
	interface LazyStatic {
        foo(a: number);
	}
    export var a;
}

declare var Lazy: LazyJS.LazyStatic;

declare module 'lazy.js' {
    export = Lazy;
}
