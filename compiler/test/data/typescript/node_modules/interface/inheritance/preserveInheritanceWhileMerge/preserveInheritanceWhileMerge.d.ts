/// <reference path="./_api.d.ts"/>

declare module "api" {
  import commonapi = require("_commonapi");

  export interface Platform<T> extends commonapi.GeneralPlatform {
    ping(): T;
  }

  var Platform: {
    new <T>(type: string, length?: number): Platform<T>;
    <T>(type: string, length?: number): Platform<T>;
  };
}