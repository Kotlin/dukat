/// <reference path="./_core.d.ts" />
/// <reference path="./_events.d.ts" />

declare module "mydomain" {
  import {SomeEvent} from "myevents";

  class MyDomain implements Core.SomeDomain {
    add(evt: SomeEvent);
  }
}