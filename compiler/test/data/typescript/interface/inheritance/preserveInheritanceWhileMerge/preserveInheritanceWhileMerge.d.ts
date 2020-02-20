/// <reference path="./_api.d.ts"/>

declare module "api" {
  import commonapi = require("_commonapi");

  export interface Platform<T> extends commonapi.GeneralPlatform {
    ping(): T;
  }
}