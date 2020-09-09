/// <reference path="./_api.d.ts"/>

declare module "api" {

  export interface Platform<T> {
    (resourceId:string, hash?:any, callback?:Function): void;
  }
}