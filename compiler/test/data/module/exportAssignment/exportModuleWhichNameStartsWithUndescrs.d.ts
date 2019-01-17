// based on react.d.ts
declare namespace __React {
    export function dummy();
}

declare module "react" {
    export = __React;
}
