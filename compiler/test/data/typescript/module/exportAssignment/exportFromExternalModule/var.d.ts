// based on mixto.d.ts
declare module Mixto {
  interface IMixinStatic {
    includeInto(constructor: any): void;
    extend(obj: any): void;
  }
}

declare module "mixto" {
  var _tmp: Mixto.IMixinStatic;
  export = _tmp;
}
